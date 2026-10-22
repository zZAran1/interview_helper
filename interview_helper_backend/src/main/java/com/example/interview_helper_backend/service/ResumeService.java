package com.example.interview_helper_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.interview_helper_backend.model.dto.ResumeAllDTO;
import com.example.interview_helper_backend.model.dto.ResumeDTO;
import com.example.interview_helper_backend.model.entity.Resume;
import com.example.interview_helper_backend.model.vo.ResumeVO;


public interface ResumeService extends IService<Resume> {
    void createResume(ResumeDTO dto);
    ResumeVO viewResume();
    void updateResume(ResumeDTO dto);
    void deleteResume();
    int getResumeId();
    ResumeAllDTO getResumeAll();
}
