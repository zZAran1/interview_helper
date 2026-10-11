package com.example.interview_helper_backend.service.impl;

import com.example.interview_helper_backend.model.dto.OptimizeContentDTO;
import com.example.interview_helper_backend.model.vo.OptimizeContentVO;
import com.example.interview_helper_backend.service.ResumeOptimizeService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;


@Service
public class ResumeOptimizeServiceImpl implements ResumeOptimizeService {
    private final ChatClient chatClient;
    public ResumeOptimizeServiceImpl(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }
    @Override
    public OptimizeContentVO optimizeResume(OptimizeContentDTO dto) {
        OptimizeContentVO vo = new OptimizeContentVO();
        vo.setSummary(optimizeContent(dto.getSummary()));
        vo.setProject_description(optimizeContent(dto.getProject_description()));
        vo.setEducation_appendix(optimizeContent(dto.getEducation_appendix()));
        vo.setWork_description(optimizeContent(dto.getWork_description()));
        return vo;
    }
    private String optimizeContent(String content) {
        if(content==null || content.isBlank()){
            return content;
        }
        String systemPrompt ="你是一位拥有10年互联网大厂经验的资深简历优化专家。\n" +
                "请优化以下简历中的这段描述文本，要求：\n" +
                "1. 将平淡的动词（如'负责'）改为行动导向词（如'主导''设计''推动'）。\n" +
                "2. 尽量融入STAR原则和可量化成果。若原文没有具体数字，可根据上下文合理推断，但需加上'约'或'近'字。\n" +
                "3. 严禁捏造不存在的工作内容或技术栈，只润色表达，不改变原意。\n" +
                "4. 只输出优化后的纯文本内容，禁止加任何解释、前后缀、引号或Markdown标记。";
        return chatClient.prompt()
                .system(systemPrompt)
                .user(content)
                .call()
                .content();
    }
}
