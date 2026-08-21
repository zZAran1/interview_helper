package com.example.interview_helper_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.interview_helper_backend.model.dto.InterviewDTO;
import com.example.interview_helper_backend.model.dto.SessionMessageDTO;
import com.example.interview_helper_backend.model.entity.Interview;
import com.example.interview_helper_backend.model.vo.InterviewVO;
import com.example.interview_helper_backend.model.vo.SessionMessageVO;

import java.util.List;

public interface InterviewService extends IService<Interview> {
    InterviewVO startInterview();
    List<InterviewVO> viewInterview();
    void deleteInterview(InterviewDTO dto);
    InterviewVO endInterview(InterviewDTO dto);
    List<SessionMessageVO> sendMessage(SessionMessageDTO dto);
    List<SessionMessageVO> viewMessages(Integer interviewId);
}
