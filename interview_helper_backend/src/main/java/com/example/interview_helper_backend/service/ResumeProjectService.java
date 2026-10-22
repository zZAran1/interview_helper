package com.example.interview_helper_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.interview_helper_backend.model.dto.ResumeProjectDTO;
import com.example.interview_helper_backend.model.entity.ResumeProject;
import com.example.interview_helper_backend.model.vo.ResumeProjectVO;

import java.util.List;

public interface ResumeProjectService extends IService<ResumeProject> {
    void createResumeProject(ResumeProjectDTO dto);
    List<ResumeProjectVO> viewResumeProject();
    void updateResumeProject(ResumeProjectDTO dto);
    void deleteResumeProject(ResumeProjectDTO dto);
}
