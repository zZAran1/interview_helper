package com.example.interview_helper_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.interview_helper_backend.model.entity.SessionMessage;

import java.util.List;

public interface SessionMessageService extends IService<SessionMessage> {
    //生成面试首个问题
    String getFirstQuestion(String resumeContext);
    //保存一条AI消息
    void saveAiMessage(Integer interviewId, String text);
    //保存用户回答并调用AI生成回复
    void reply(Integer interviewId, String userText);
    //查询某场面试的全部消息（按时间正序）
    List<SessionMessage> listMessages(Integer interviewId);
    //拼接面试对话文本
    String buildConversationText(Integer interviewId);
    //调用AI对整场面试进行评估
    String evaluate(String conversationText);
    //删除某场面试的全部消息
    void deleteByInterviewId(Integer interviewId);
}
