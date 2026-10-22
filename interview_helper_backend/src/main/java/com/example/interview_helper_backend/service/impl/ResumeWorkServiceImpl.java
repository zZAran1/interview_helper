package com.example.interview_helper_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.interview_helper_backend.converter.Converter;
import com.example.interview_helper_backend.mapper.ResumeWorkMapper;
import com.example.interview_helper_backend.model.dto.ResumeWorkDTO;
import com.example.interview_helper_backend.model.entity.ResumeWork;
import com.example.interview_helper_backend.model.vo.ResumeWorkVO;
import com.example.interview_helper_backend.service.ResumeService;
import com.example.interview_helper_backend.service.ResumeWorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor// 自动生成包含 final 字段的构造器
public class ResumeWorkServiceImpl extends ServiceImpl<ResumeWorkMapper, ResumeWork> implements ResumeWorkService {
    private final ResumeService resumeService;
    @Override
    public void createResumeWork(ResumeWorkDTO dto){
        ResumeWork resumeWork = Converter.INSTANCE.toResumeWork(dto);
        resumeWork.setResume_id(resumeService.getResumeId());
        this.save(resumeWork);
    }
    @Override
    public List<ResumeWorkVO> viewResumeWork(){
        List<ResumeWork> resumeWork = this.lambdaQuery()
                .eq(ResumeWork::getResume_id,resumeService.getResumeId())
                .list();
        return resumeWork.stream()
                .map(Converter.INSTANCE::toResumeWorkVO)
                .collect(Collectors.toList());
    }
    @Override
    public void updateResumeWork(ResumeWorkDTO dto){
        this.lambdaUpdate()
                .eq(ResumeWork::getResume_id,resumeService.getResumeId())
                .eq(ResumeWork::getId,dto.getId())
                .set(ResumeWork::getCompany_name,dto.getCompany_name())
                .set(ResumeWork::getPosition,dto.getPosition())
                .set(ResumeWork::getStart_date,dto.getStart_date())
                .set(ResumeWork::getEnd_date,dto.getEnd_date())
                .set(ResumeWork::getDescription,dto.getDescription())
                .update();
    }
    @Override
    public void deleteResumeWork(ResumeWorkDTO dto){
        this.lambdaUpdate()
                .eq(ResumeWork::getResume_id,resumeService.getResumeId())
                .eq(ResumeWork::getId,dto.getId())
                .remove();
    }
}
