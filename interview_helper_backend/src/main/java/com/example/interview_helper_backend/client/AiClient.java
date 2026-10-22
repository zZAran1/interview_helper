package com.example.interview_helper_backend.client;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import java.time.Duration;
import com.example.interview_helper_backend.exception.AiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Component
public class AiClient {

    public record ContentResponse(String content) {
    }

    public record OptimizeResponse(String summary, String project_description,
                                   String education_appendix, String work_description) {
    }

    public record OptimizeRequest(String summary, String project_description,
                                  String education_appendix, String work_description) {
    }

    private final RestClient restClient;
        // 同时进行的 AI 调用上限，超出的请求排队等待
    private static final int MAX_CONCURRENT_AI_CALLS = 20;
    // 排队等待上限 10 秒：10s 排队 + 110s 读超时 = 120s，不超前端超时预算
    private static final long QUEUE_WAIT_SECONDS = 10;
    private final Semaphore aiSemaphore = new Semaphore(MAX_CONCURRENT_AI_CALLS);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiClient(@Value("${ai.service.base-url}") String baseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));   // 连不上 5 秒内快速失败
        factory.setReadTimeout(Duration.ofSeconds(110));    // 小于前端 120s，保证用户收到友好提示
        this.restClient = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    public String firstQuestion(String resumeContext) {
        return post("/ai/interview/first-question", new ContentRequest(resumeContext, null), ContentResponse.class).content();
    }

    public String reply(String conversation) {
        return post("/ai/interview/reply", new ContentRequest(null, conversation), ContentResponse.class).content();
    }

    public String evaluate(String conversation) {
        return post("/ai/interview/evaluate", new ContentRequest(null, conversation), ContentResponse.class).content();
    }

    public OptimizeResponse optimize(String summary, String project_description,
                                     String education_appendix, String work_description) {
        OptimizeRequest body = new OptimizeRequest(summary, project_description, education_appendix, work_description);
        return post("/ai/resume/optimize", body, OptimizeResponse.class);
    }

    private record ContentRequest(String resumeContext, String conversation) {
    }

    private <T> T post(String uri, Object body, Class<T> responseType) {
        boolean acquired;
        try {
            acquired = aiSemaphore.tryAcquire(QUEUE_WAIT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AiException("AI服务繁忙，请稍后再试");
        }
        if (!acquired) {
            // 排队 10 秒还没等到名额：友好降级，保护 AI 服务和上游 API 不被打挂
            throw new AiException("当前使用人数较多，请稍后再试");
        }
        try {
            for (int attempt = 1; attempt <= 2; attempt++) {
                try {
                    T resp = restClient.post()
                            .uri(uri)
                            .body(body)
                            .retrieve()
                            .body(responseType);
                    if (resp == null) {
                        throw new AiException("AI生成失败，请重试");
                    }
                    return resp;
                } catch (AiException e) {
                    throw e;
                } catch (RestClientResponseException e) {
                    // AI服务返回了错误响应（如Key失效），重试没意义，直接透传提示
                    throw new AiException(extractDetail(e.getResponseBodyAsString()));
                } catch (Exception e) {
                    // 只有"连不上"才重试；读超时说明请求已发出、时间预算已耗尽，不重试
                    if (attempt == 1 && isConnectError(e)) {
                        continue;
                    }
                    throw new AiException("AI服务连接失败，请稍后重试");
                }
            }
            throw new AiException("AI服务连接失败，请稍后重试");
        } finally {
            aiSemaphore.release();   // 无论成败都必须归还名额，否则额度会被耗尽
        }
    }

    private String extractDetail(String errorBody) {
        try {
            JsonNode node = objectMapper.readTree(errorBody);
            String detail = node.path("detail").asText("");
            if (!detail.isBlank()) {
                return detail;
            }
        } catch (Exception ignored) {
            //错误体不是JSON时走下方默认提示
        }
        return "AI服务调用失败，请稍后重试";
    }

    private boolean isConnectError(Exception e) {
        Throwable cause = e;
        while (cause != null) {
            if (cause instanceof java.net.ConnectException
                    || cause instanceof java.net.UnknownHostException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}