package com.example.interview_helper_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.interview_helper_backend.converter.Converter;
import com.example.interview_helper_backend.mapper.ResumeProjectMapper;
import com.example.interview_helper_backend.model.dto.ResumeProjectDTO;
import com.example.interview_helper_backend.model.entity.ResumeProject;
import com.example.interview_helper_backend.model.vo.ResumeProjectVO;
import com.example.interview_helper_backend.service.ResumeProjectService;
import com.example.interview_helper_backend.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor// 自动生成包含 final 字段的构造器
public class ResumeProjectServiceImpl extends ServiceImpl<ResumeProjectMapper, ResumeProject> implements ResumeProjectService {
    private final ResumeService resumeService;
    @Override
    public void createResumeProject(ResumeProjectDTO dto){
        ResumeProject resumeProject = Converter.INSTANCE.toResumeProject(dto);
        resumeProject.setResume_id(resumeService.getResumeId());
        this.save(resumeProject);
    }
    @Override
    public List<ResumeProjectVO> viewResumeProject(){
        List<ResumeProject> resumeProject = this.lambdaQuery()
                .eq(ResumeProject::getResume_id,resumeService.getResumeId())
                .list();
        return resumeProject.stream()
                .map(Converter.INSTANCE::toResumeProjectVO)
                .collect(Collectors.toList());
    }
    @Override
    public void updateResumeProject(ResumeProjectDTO dto){
        this.lambdaUpdate()
                .eq(ResumeProject::getResume_id,resumeService.getResumeId())
                .eq(ResumeProject::getId,dto.getId())
                .set(ResumeProject::getProject_name,dto.getProject_name())
                .set(ResumeProject::getRole,dto.getRole())
                .set(ResumeProject::getStart_date,dto.getStart_date())
                .set(ResumeProject::getEnd_date,dto.getEnd_date())
                .set(ResumeProject::getDescription,dto.getDescription())
                .update();
    }
    @Override
    public void deleteResumeProject(ResumeProjectDTO dto){
        this.lambdaUpdate()
                .eq(ResumeProject::getResume_id,resumeService.getResumeId())
                .eq(ResumeProject::getId,dto.getId())
                .remove();
    }
}
