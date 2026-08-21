package com.example.interview_helper_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.interview_helper_backend.model.dto.ResumeEducationDTO;
import com.example.interview_helper_backend.model.entity.ResumeEducation;
import com.example.interview_helper_backend.model.vo.ResumeEducationVO;

import java.util.List;

public interface ResumeEducationService extends IService<ResumeEducation> {
    void createResumeEducation(ResumeEducationDTO dto);
    List<ResumeEducationVO> viewResumeEducation();
    void updateResumeEducation(ResumeEducationDTO dto);
    void deleteResumeEducation(ResumeEducationDTO dto);
}
