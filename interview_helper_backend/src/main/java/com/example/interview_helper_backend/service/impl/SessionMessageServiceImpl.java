package com.example.interview_helper_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.interview_helper_backend.mapper.SessionMessageMapper;
import com.example.interview_helper_backend.model.entity.SessionMessage;
import com.example.interview_helper_backend.service.SessionMessageService;
import org.springframework.stereotype.Service;
import com.example.interview_helper_backend.client.AiClient;
import java.util.concurrent.locks.ReentrantLock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.example.interview_helper_backend.util.InterviewLocks;
@Service
public class SessionMessageServiceImpl extends ServiceImpl<SessionMessageMapper, SessionMessage> implements SessionMessageService {

    private static final int MAX_MESSAGE_LENGTH = 1024;
    //拼进AI上下文的最大消息条数：只取最近N条，防止长对话超出模型上下文限制
    private static final int MAX_CONTEXT_MESSAGES = 20;

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
        ReentrantLock lock = InterviewLocks.getLock(interviewId);
        lock.lock();
        try {
            SessionMessage userMessage = new SessionMessage();
            userMessage.setInterview_id(interviewId);
            userMessage.setRole(SessionMessage.Role.user);
            userMessage.setMessagetext(userText);
            userMessage.setSend_date(LocalDateTime.now());
            this.save(userMessage);

            String conversation = buildConversationText(interviewId);
            String aiText = aiClient.reply(conversation);
            saveAiMessage(interviewId, aiText);
        } finally {
            lock.unlock();   // 必须放在 finally，异常时也要释放，否则这场面试永久卡死
        }
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
        List<SessionMessage> messages = listMessages(interviewId);
        //只取最近N条拼进上下文，防止长对话超出模型上下文限制
        if (messages.size() > MAX_CONTEXT_MESSAGES) {
            messages = messages.subList(messages.size() - MAX_CONTEXT_MESSAGES, messages.size());
        }
        return messages.stream()
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
