package com.example.interview_helper_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.interview_helper_backend.model.dto.ResumeWorkDTO;
import com.example.interview_helper_backend.model.entity.ResumeWork;
import com.example.interview_helper_backend.model.vo.ResumeWorkVO;

import java.util.List;

public interface ResumeWorkService extends IService<ResumeWork> {
    void createResumeWork(ResumeWorkDTO dto);
    List<ResumeWorkVO> viewResumeWork();
    void updateResumeWork(ResumeWorkDTO dto);
    void deleteResumeWork(ResumeWorkDTO dto);
}
