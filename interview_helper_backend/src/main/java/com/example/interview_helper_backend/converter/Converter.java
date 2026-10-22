package com.example.interview_helper_backend.converter;

import com.example.interview_helper_backend.model.dto.*;
import com.example.interview_helper_backend.model.entity.*;
import com.example.interview_helper_backend.model.vo.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
@Mapper
public interface Converter {
    Converter INSTANCE = Mappers.getMapper(Converter.class);
    @Mapping(source = "password", target = "password_hash")
    User toUser(UserRegisterDTO dto);
    UserProfileVO toVO(User user);
    Resume toResume(ResumeDTO dto);
    ResumeDTO toResumeDTO(Resume resume);
    ResumeVO toVO(Resume resume);
    ResumeEducation toResumeEducation(ResumeEducationDTO dto);
    ResumeEducationDTO toResumeEducationDTO(ResumeEducation resumeEducation);
    ResumeEducationVO toResumeEducationVO(ResumeEducation resumeEducation);
    ResumeProject toResumeProject(ResumeProjectDTO dto);
    ResumeProjectDTO toResumeProjectDTO(ResumeProject resumeProject);
    ResumeProjectVO toResumeProjectVO(ResumeProject resumeProject);
    ResumeWork toResumeWork(ResumeWorkDTO dto);
    ResumeWorkDTO toResumeWorkDTO(ResumeWork resumeWork);
    ResumeWorkVO toResumeWorkVO(ResumeWork resumeWork);
    InterviewVO toInterviewVO(Interview interview);
    SessionMessageVO toSessionMessageVO(SessionMessage sessionMessage);
}
