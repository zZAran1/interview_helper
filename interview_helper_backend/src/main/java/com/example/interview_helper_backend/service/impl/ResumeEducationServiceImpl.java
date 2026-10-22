package com.example.interview_helper_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.interview_helper_backend.converter.Converter;
import com.example.interview_helper_backend.mapper.ResumeEducationMapper;
import com.example.interview_helper_backend.model.dto.ResumeEducationDTO;
import com.example.interview_helper_backend.model.entity.ResumeEducation;
import com.example.interview_helper_backend.model.vo.ResumeEducationVO;
import com.example.interview_helper_backend.service.ResumeEducationService;
import com.example.interview_helper_backend.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor// 自动生成包含 final 字段的构造器
public class ResumeEducationServiceImpl extends ServiceImpl<ResumeEducationMapper, ResumeEducation> implements ResumeEducationService {
    private final ResumeService resumeService;
    @Override
    public void createResumeEducation(ResumeEducationDTO dto){
        ResumeEducation resumeEducation= Converter.INSTANCE.toResumeEducation(dto);
        resumeEducation.setResume_id(resumeService.getResumeId());
        this.save(resumeEducation);
    };
    @Override
    public List<ResumeEducationVO> viewResumeEducation(){
        List<ResumeEducation> resumeEducation= this.lambdaQuery()
                        .eq(ResumeEducation::getResume_id,resumeService.getResumeId()).list();
        return resumeEducation.stream()
                .map(Converter.INSTANCE::toResumeEducationVO)
                .collect(Collectors.toList());
    };
    @Override
    public void updateResumeEducation(ResumeEducationDTO dto){
        this.lambdaUpdate()
                .eq(ResumeEducation::getResume_id,resumeService.getResumeId())
                .eq(ResumeEducation::getId,dto.getId())
                .set(ResumeEducation::getSchool_name,dto.getSchool_name())
                .set(ResumeEducation::getDegree,dto.getDegree())
                .set(ResumeEducation::getField_of_study,dto.getField_of_study())
                .set(ResumeEducation::getStart_date,dto.getStart_date())
                .set(ResumeEducation::getEnd_date,dto.getEnd_date())
                .set(ResumeEducation::getCore_courses,dto.getCore_courses())
                .set(ResumeEducation::getAppendix,dto.getAppendix())
                .update();
    };
    @Override
    public void deleteResumeEducation(ResumeEducationDTO dto){
        this.lambdaUpdate()
                .eq(ResumeEducation::getResume_id,resumeService.getResumeId())
                .eq(ResumeEducation::getId,dto.getId())
                .remove();
    }
}
