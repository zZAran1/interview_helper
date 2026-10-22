package com.example.interview_helper_backend.client;

import com.example.interview_helper_backend.exception.AiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

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
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiClient(@Value("${ai.service.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
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
            //AI服务返回了错误响应，提取其中的detail作为提示（如"AI服务认证失败，请检查API Key配置"）
            throw new AiException(extractDetail(e.getResponseBodyAsString()));
        } catch (Exception e) {
            //网络不通、连接超时等兜底
            throw new AiException("AI服务连接失败，请稍后重试");
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
}