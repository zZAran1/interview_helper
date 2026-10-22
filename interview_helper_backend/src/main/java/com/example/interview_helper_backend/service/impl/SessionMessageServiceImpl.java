package com.example.interview_helper_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.interview_helper_backend.exception.AiException;
import com.example.interview_helper_backend.mapper.SessionMessageMapper;
import com.example.interview_helper_backend.model.entity.SessionMessage;
import com.example.interview_helper_backend.service.SessionMessageService;
import org.springframework.stereotype.Service;
import com.example.interview_helper_backend.client.AiClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionMessageServiceImpl extends ServiceImpl<SessionMessageMapper, SessionMessage> implements SessionMessageService {

    private static final int MAX_MESSAGE_LENGTH = 1024;

    private final AiClient aiClient;

    public SessionMessageServiceImpl(AiClient aiClient) {
        this.aiClient = aiClient;
    }

    @Override
    public String getFirstQuestion(String resumeContext) {
        return aiClient.firstQuestion(resumeContext);
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
        String aiText = aiClient.reply(conversation);
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
        return aiClient.evaluate(conversationText);
    }

    @Override
    public void deleteByInterviewId(Integer interviewId) {
        this.lambdaUpdate()
                .eq(SessionMessage::getInterview_id, interviewId)
                .remove();
    }

    private String truncate(String text) {
        if (text == null) {
            return "";
        }
        return text.length() <= MAX_MESSAGE_LENGTH ? text : text.substring(0, MAX_MESSAGE_LENGTH);
    }
}
