package com.example.interview_helper_backend.controller;


import com.example.interview_helper_backend.model.dto.InterviewDTO;
import com.example.interview_helper_backend.model.dto.SessionMessageDTO;
import com.example.interview_helper_backend.model.vo.InterviewVO;
import com.example.interview_helper_backend.model.vo.SessionMessageVO;
import com.example.interview_helper_backend.result.Result;
import com.example.interview_helper_backend.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
@Validated
public class InterviewController {
    private final InterviewService interviewService;

    @PostMapping("/startInterview")
    public Result<InterviewVO> startInterview() {
        InterviewVO vo = interviewService.startInterview();
        return Result.success("开始面试成功", vo);
    }
    @GetMapping("/viewInterview")
    public Result<List<InterviewVO>> viewInterview() {
        List<InterviewVO> list = interviewService.viewInterview();
        return Result.success(list);
    }
    @DeleteMapping("/deleteInterview")
    public Result<Void> deleteInterview(@RequestBody @Validated InterviewDTO dto) {
        interviewService.deleteInterview(dto);
        return Result.success("删除成功");
    }
    @PostMapping("/sendMessage")
    public Result<List<SessionMessageVO>> sendMessage(@RequestBody @Validated SessionMessageDTO dto) {
        List<SessionMessageVO> list = interviewService.sendMessage(dto);
        return Result.success(list);
    }
    @GetMapping("/viewMessages")
    public Result<List<SessionMessageVO>> viewMessages(@RequestParam("interviewId") Integer interviewId) {
        List<SessionMessageVO> list = interviewService.viewMessages(interviewId);
        return Result.success(list);
    }
    @PostMapping("/endInterview")
    public Result<InterviewVO> endInterview(@RequestBody @Validated InterviewDTO dto) {
        InterviewVO vo = interviewService.endInterview(dto);
        return Result.success("评估完成", vo);
    }
}
