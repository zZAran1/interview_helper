package com.example.interview_helper_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.interview_helper_backend.exception.AiException;
import com.example.interview_helper_backend.mapper.SessionMessageMapper;
import com.example.interview_helper_backend.model.entity.SessionMessage;
import com.example.interview_helper_backend.service.SessionMessageService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionMessageServiceImpl extends ServiceImpl<SessionMessageMapper, SessionMessage> implements SessionMessageService {

    private static final int MAX_MESSAGE_LENGTH = 1024;

    private static final String INTERVIEWER_SYSTEM_PROMPT =
            "你是一位拥有10年互联网大厂面试官经验的资深模拟面试官。\n" +
            "【面试规则】\n" +
            "1. 每次只提出一个问题，等待求职者回答后再继续。\n" +
            "2. 求职者回答后，先给出一句简短的针对性点评（指出亮点与不足），再提出下一个问题。\n" +
            "3. 提问要贴合简历内容和求职意向，从自我介绍、项目经历、技术深度、行为面试等方面循序渐进。\n" +
            "4. 当求职者明确表示想结束面试（如“结束面试”“就到这里”）时，应停止提问并简短总结表现。\n" +
            "5. 回复控制在300字以内，语言专业、友好。\n" +
            "6. 全程使用中文。";

    private static final String EVALUATION_SYSTEM_PROMPT =
            "你是一位拥有10年互联网大厂面试官经验的资深模拟面试官，请根据面试记录对求职者的整体表现进行评估。\n" +
            "请严格按以下两行格式输出评估结果，不要输出其他任何内容：\n" +
            "评分：X（0-10的数值，可含一位小数）\n" +
            "建议：（给出2-4条具体、可执行的改进建议）";

    private final ChatClient chatClient;

    public SessionMessageServiceImpl(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @Override
    public String getFirstQuestion(String resumeContext) {
        String userContent = (resumeContext == null || resumeContext.isBlank())
                ? "请开始模拟面试，向求职者提出第一个问题。"
                : "以下是求职者的简历信息：\n" + resumeContext + "\n请基于简历信息开始模拟面试，提出第一个问题。";
        return callAi(INTERVIEWER_SYSTEM_PROMPT, userContent);
    }

    @Override
    public void saveAiMessage(Integer interviewId, String text) {
        SessionMessage message = new SessionMessage();
        message.setInterview_id(interviewId);
        message.setRole(SessionMessage.Role.ai);
        message.setMessagetext(truncate(text));
        message.setSend_date(LocalDateTime.now());
        this.save(message);
    }

    @Override
    public void reply(Integer interviewId, String userText) {
        SessionMessage userMessage = new SessionMessage();
        userMessage.setInterview_id(interviewId);
        userMessage.setRole(SessionMessage.Role.user);
        userMessage.setMessagetext(userText);
        userMessage.setSend_date(LocalDateTime.now());
        this.save(userMessage);

        String conversation = buildConversationText(interviewId);
        String aiText = callAi(INTERVIEWER_SYSTEM_PROMPT,
                "以下是本次面试的对话记录：\n" + conversation + "\n请针对求职者的最新回答进行简短点评，并提出下一个问题。");
        saveAiMessage(interviewId, aiText);
    }

    @Override
    public List<SessionMessage> listMessages(Integer interviewId) {
        return this.lambdaQuery()
                .eq(SessionMessage::getInterview_id, interviewId)
                .orderByAsc(SessionMessage::getId)
                .list();
    }

    @Override
    public String buildConversationText(Integer interviewId) {
        return listMessages(interviewId).stream()
                .map(message -> (message.getRole() == SessionMessage.Role.ai ? "面试官：" : "求职者：") + message.getMessagetext())
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String evaluate(String conversationText) {
        return callAi(EVALUATION_SYSTEM_PROMPT, "【面试记录】\n" + conversationText);
    }

    @Override
    public void deleteByInterviewId(Integer interviewId) {
        this.lambdaUpdate()
                .eq(SessionMessage::getInterview_id, interviewId)
                .remove();
    }

    private String callAi(String systemPrompt, String userContent) {
        try {
            String content = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userContent)
                    .call()
                    .content();
            if (content == null || content.isBlank()) {
                throw new AiException("AI生成失败，请重试");
            }
            return content;
        } catch (AiException e) {
            throw e;
        } catch (Exception e) {
            throw new AiException("AI服务调用失败，请稍后重试");
        }
    }

    private String truncate(String text) {
        if (text == null) {
            return "";
        }
        return text.length() <= MAX_MESSAGE_LENGTH ? text : text.substring(0, MAX_MESSAGE_LENGTH);
    }
}
