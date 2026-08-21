package com.example.interview_helper_backend.controller;

import com.example.interview_helper_backend.model.dto.*;
import com.example.interview_helper_backend.model.vo.*;
import com.example.interview_helper_backend.result.Result;
import com.example.interview_helper_backend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
@Validated
public class ResumeController {
    private final ResumeService resumeService;
    private final ResumeEducationService resumeEducationService;
    private final ResumeProjectService resumeProjectService;
    private final ResumeWorkService resumeWorkService;
    private final ResumeOptimizeService resumeOptimizeService;

    //简历 个人信息
    @PostMapping("/createResume")
    public Result<Void> createResume(@Validated @RequestBody ResumeDTO dto) {
        resumeService.createResume(dto);
        return Result.success("创建简历成功");
    }
    @GetMapping("/viewResume")
    public Result<ResumeVO> viewResume() {
        ResumeVO resumeVO=resumeService.viewResume();
        return Result.success(resumeVO);
    }
    @PutMapping("/updateResume")
    public Result<Void> updateResume(@Validated @RequestBody ResumeDTO dto) {
        resumeService.updateResume(dto);
        return Result.success("保存成功");
    }
    @DeleteMapping("/deleteResume")
    public Result<Void> deleteResume() {
        resumeService.deleteResume();
        return Result.success("删除成功");
    }

    //简历 教育经历
    @PostMapping("/createResumeEducation")
    public Result<Void> createResumeEducation(@Validated @RequestBody ResumeEducationDTO dto) {
        resumeEducationService.createResumeEducation(dto);
        return Result.success("添加教育经历成功");
    }
    @GetMapping("/viewResumeEducation")
    public Result<List<ResumeEducationVO>> viewResumeEducation() {
        List<ResumeEducationVO> list=resumeEducationService.viewResumeEducation();
        return Result.success(list);
    }
    @PutMapping("/updateResumeEducation")
    public Result<Void> updateResumeEducation(@Validated @RequestBody ResumeEducationDTO dto) {
        resumeEducationService.updateResumeEducation(dto);
        return Result.success("保存成功");
    }
    @DeleteMapping("/deleteResumeEducation")
    public Result<Void> deleteResumeEducation(@Validated @RequestBody ResumeEducationDTO dto) {
        resumeEducationService.deleteResumeEducation(dto);
        return Result.success("删除成功");
    }

    //简历 项目经历
    @PostMapping("/createResumeProject")
    public Result<Void> createResumeProject(@Validated @RequestBody ResumeProjectDTO dto) {
        resumeProjectService.createResumeProject(dto);
        return Result.success("添加成功");
    }
    @GetMapping("/viewResumeProject")
    public Result<List<ResumeProjectVO>> viewResumeProject() {
        List<ResumeProjectVO> list=resumeProjectService.viewResumeProject();
        return Result.success(list);
    }
    @PutMapping("/updateResumeProject")
    public Result<Void> updateResumeProject(@Validated @RequestBody ResumeProjectDTO dto) {
        resumeProjectService.updateResumeProject(dto);
        return Result.success("保存成功");
    }
    @DeleteMapping("/deleteResumeProject")
    public Result<Void> deleteResumeProject(@Validated @RequestBody ResumeProjectDTO dto) {
        resumeProjectService.deleteResumeProject(dto);
        return Result.success("删除成功");
    }

    //简历 工作经历
    @PostMapping("/createResumeWork")
    public Result<Void> createResumeWork(@Validated @RequestBody ResumeWorkDTO dto) {
        resumeWorkService.createResumeWork(dto);
        return Result.success("添加成功");
    }
    @GetMapping("/viewResumeWork")
    public Result<List<ResumeWorkVO>> viewResumeWork() {
        List<ResumeWorkVO> list=resumeWorkService.viewResumeWork();
        return Result.success(list);
    }
    @PutMapping("/updateResumeWork")
    public Result<Void> updateResumeWork(@Validated @RequestBody ResumeWorkDTO dto) {
        resumeWorkService.updateResumeWork(dto);
        return Result.success("保存成功");
    }
    @DeleteMapping("/deleteResumeWork")
    public Result<Void> deleteResumeWork(@Validated @RequestBody ResumeWorkDTO dto) {
        resumeWorkService.deleteResumeWork(dto);
        return Result.success("删除成功");
    }

    //优化简历
    @PostMapping("/optimizeResume")
    public Result<OptimizeContentVO> optimizeResume(@Validated @RequestBody OptimizeContentDTO dto) {
        OptimizeContentVO vo = resumeOptimizeService.optimizeResume(dto);
        return Result.success("优化简历成功",vo);
    }
}
