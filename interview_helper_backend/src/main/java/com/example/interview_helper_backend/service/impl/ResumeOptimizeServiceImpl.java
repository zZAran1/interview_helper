package com.example.interview_helper_backend.service.impl;

import com.example.interview_helper_backend.client.AiClient;
import com.example.interview_helper_backend.model.dto.OptimizeContentDTO;
import com.example.interview_helper_backend.model.vo.OptimizeContentVO;
import com.example.interview_helper_backend.service.ResumeOptimizeService;
import org.springframework.stereotype.Service;

@Service
public class ResumeOptimizeServiceImpl implements ResumeOptimizeService {

    private final AiClient aiClient;

    public ResumeOptimizeServiceImpl(AiClient aiClient) {
        this.aiClient = aiClient;
    }

    @Override
    public OptimizeContentVO optimizeResume(OptimizeContentDTO dto) {
        AiClient.OptimizeResponse resp = aiClient.optimize(
                dto.getSummary(),
                dto.getProject_description(),
                dto.getEducation_appendix(),
                dto.getWork_description());

        OptimizeContentVO vo = new OptimizeContentVO();
        vo.setSummary(resp.summary());
        vo.setProject_description(resp.project_description());
        vo.setEducation_appendix(resp.education_appendix());
        vo.setWork_description(resp.work_description());
        return vo;
    }
}