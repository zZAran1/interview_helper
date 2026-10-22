package com.example.interview_helper_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.interview_helper_backend.converter.Converter;
import com.example.interview_helper_backend.exception.ResumeException;
import com.example.interview_helper_backend.exception.TokenException;
import com.example.interview_helper_backend.mapper.ResumeEducationMapper;
import com.example.interview_helper_backend.mapper.ResumeMapper;
import com.example.interview_helper_backend.mapper.ResumeProjectMapper;
import com.example.interview_helper_backend.mapper.ResumeWorkMapper;
import com.example.interview_helper_backend.model.dto.ResumeAllDTO;
import com.example.interview_helper_backend.model.dto.ResumeDTO;
import com.example.interview_helper_backend.model.entity.Resume;
import com.example.interview_helper_backend.model.entity.ResumeEducation;
import com.example.interview_helper_backend.model.entity.ResumeProject;
import com.example.interview_helper_backend.model.entity.ResumeWork;
import com.example.interview_helper_backend.model.vo.ResumeVO;
import com.example.interview_helper_backend.service.ResumeService;
import com.example.interview_helper_backend.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl extends ServiceImpl<ResumeMapper, Resume> implements ResumeService {
    private final ResumeEducationMapper resumeEducationMapper;
    private final ResumeProjectMapper resumeProjectMapper;
    private final ResumeWorkMapper resumeWorkMapper;
    @Override
    public void createResume(ResumeDTO dto) {
        int user_id = UserContext.getUserId();
        if(this.lambdaQuery().eq(Resume::getUser_id,user_id).exists()){
            throw new ResumeException("一个用户对应一份简历");
        }
        Resume resume= Converter.INSTANCE.toResume(dto);
        resume.setUser_id(user_id);
        this.save(resume);
    }
    @Override
    public ResumeVO viewResume(){
        int user_id=UserContext.getUserId();
        Resume resume=this.lambdaQuery()
                .eq(Resume::getUser_id,user_id)
                .one();
        return Converter.INSTANCE.toVO(resume);
    }
    @Override
    public void updateResume(ResumeDTO dto){
        int user_id=UserContext.getUserId();
        Resume resume= Converter.INSTANCE.toResume(dto);
        resume.setUser_id(user_id);
        this.lambdaUpdate()
                .eq(Resume::getUser_id,user_id)
                .set(Resume::getReal_name,resume.getReal_name())
                .set(Resume::getAge,resume.getAge())
                .set(Resume::getGender,resume.getGender())
                .set(Resume::getPhone_number,resume.getPhone_number())
                .set(Resume::getSkill,resume.getSkill())
                .set(Resume::getSummary,resume.getSummary())
                .set(Resume::getCertificate,resume.getCertificate())
                .set(Resume::getEducation,resume.getEducation())
                .set(Resume::getDesired_position,resume.getDesired_position())
                .update();
    }
    @Override
    public void deleteResume(){
        int user_id=UserContext.getUserId();
        this.lambdaUpdate()
                .eq(Resume::getUser_id,user_id)
                .remove();
    }
    @Override
    public int getResumeId(){
        int userId = UserContext.getUserId();
        if (userId <= 0) {
            throw new TokenException("用户未登录或登录状态已过期");
        }
        Resume resume = this.lambdaQuery()
                .eq(Resume::getUser_id, userId)
                .one();
        if (resume == null) {
            throw new ResumeException("请先创建简历，才能进行面试");
        }
        return resume.getId();
    }
    @Override
    public ResumeAllDTO getResumeAll(){
        int userId = UserContext.getUserId();
        ResumeAllDTO all = new ResumeAllDTO();
        Resume resume = this.lambdaQuery()
                .eq(Resume::getUser_id, userId)
                .one();
        if (resume == null) {
            return all;
        }
        all.setResumeDTO(Converter.INSTANCE.toResumeDTO(resume));

        ResumeEducation education = resumeEducationMapper.selectList(new LambdaQueryWrapper<ResumeEducation>()
                .eq(ResumeEducation::getResume_id, resume.getId())
                .orderByDesc(ResumeEducation::getId))
                .stream().findFirst().orElse(null);
        all.setResumeEducationDTO(Converter.INSTANCE.toResumeEducationDTO(education));

        ResumeProject project = resumeProjectMapper.selectList(new LambdaQueryWrapper<ResumeProject>()
                .eq(ResumeProject::getResume_id, resume.getId())
                .orderByDesc(ResumeProject::getId))
                .stream().findFirst().orElse(null);
        all.setResumeProjectDTO(Converter.INSTANCE.toResumeProjectDTO(project));

        ResumeWork work = resumeWorkMapper.selectList(new LambdaQueryWrapper<ResumeWork>()
                .eq(ResumeWork::getResume_id, resume.getId())
                .orderByDesc(ResumeWork::getId))
                .stream().findFirst().orElse(null);
        all.setResumeWorkDTO(Converter.INSTANCE.toResumeWorkDTO(work));
        return all;
    }
}
