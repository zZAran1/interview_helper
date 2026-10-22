package com.example.interview_helper_backend.service;

import com.example.interview_helper_backend.model.dto.OptimizeContentDTO;
import com.example.interview_helper_backend.model.vo.OptimizeContentVO;
public interface ResumeOptimizeService {
    OptimizeContentVO optimizeResume(OptimizeContentDTO dto);
}
