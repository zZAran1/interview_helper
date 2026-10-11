package com.example.interview_helper_backend.converter;

import com.example.interview_helper_backend.model.dto.ResumeDTO;
import com.example.interview_helper_backend.model.dto.ResumeEducationDTO;
import com.example.interview_helper_backend.model.dto.ResumeProjectDTO;
import com.example.interview_helper_backend.model.dto.ResumeWorkDTO;
import com.example.interview_helper_backend.model.dto.UserRegisterDTO;
import com.example.interview_helper_backend.model.entity.Interview;
import com.example.interview_helper_backend.model.entity.Resume;
import com.example.interview_helper_backend.model.entity.ResumeEducation;
import com.example.interview_helper_backend.model.entity.ResumeProject;
import com.example.interview_helper_backend.model.entity.ResumeWork;
import com.example.interview_helper_backend.model.entity.SessionMessage;
import com.example.interview_helper_backend.model.entity.User;
import com.example.interview_helper_backend.model.vo.InterviewVO;
import com.example.interview_helper_backend.model.vo.ResumeEducationVO;
import com.example.interview_helper_backend.model.vo.ResumeProjectVO;
import com.example.interview_helper_backend.model.vo.ResumeVO;
import com.example.interview_helper_backend.model.vo.ResumeWorkVO;
import com.example.interview_helper_backend.model.vo.SessionMessageVO;
import com.example.interview_helper_backend.model.vo.UserProfileVO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-21T17:29:06+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ConverterImpl implements Converter {

    @Override
    public User toUser(UserRegisterDTO dto) {
        if ( dto == null ) {
            return null;
        }

        User user = new User();

        user.setPassword_hash( dto.getPassword() );
        user.setUsername( dto.getUsername() );
        user.setEmail( dto.getEmail() );

        return user;
    }

    @Override
    public UserProfileVO toVO(User user) {
        if ( user == null ) {
            return null;
        }

        UserProfileVO userProfileVO = new UserProfileVO();

        userProfileVO.setUsername( user.getUsername() );
        userProfileVO.setEmail( user.getEmail() );
        userProfileVO.setCreated_at( user.getCreated_at() );

        return userProfileVO;
    }

    @Override
    public Resume toResume(ResumeDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Resume resume = new Resume();

        resume.setReal_name( dto.getReal_name() );
        resume.setAge( dto.getAge() );
        resume.setGender( dto.getGender() );
        resume.setPhone_number( dto.getPhone_number() );
        resume.setSkill( dto.getSkill() );
        resume.setSummary( dto.getSummary() );
        resume.setCertificate( dto.getCertificate() );
        resume.setEducation( dto.getEducation() );
        resume.setDesired_position( dto.getDesired_position() );

        return resume;
    }

    @Override
    public ResumeDTO toResumeDTO(Resume resume) {
        if ( resume == null ) {
            return null;
        }

        ResumeDTO resumeDTO = new ResumeDTO();

        resumeDTO.setReal_name( resume.getReal_name() );
        resumeDTO.setAge( resume.getAge() );
        resumeDTO.setGender( resume.getGender() );
        resumeDTO.setPhone_number( resume.getPhone_number() );
        resumeDTO.setSkill( resume.getSkill() );
        resumeDTO.setSummary( resume.getSummary() );
        resumeDTO.setCertificate( resume.getCertificate() );
        resumeDTO.setEducation( resume.getEducation() );
        resumeDTO.setDesired_position( resume.getDesired_position() );

        return resumeDTO;
    }

    @Override
    public ResumeVO toVO(Resume resume) {
        if ( resume == null ) {
            return null;
        }

        ResumeVO resumeVO = new ResumeVO();

        resumeVO.setReal_name( resume.getReal_name() );
        resumeVO.setAge( resume.getAge() );
        resumeVO.setGender( resume.getGender() );
        resumeVO.setPhone_number( resume.getPhone_number() );
        resumeVO.setSkill( resume.getSkill() );
        resumeVO.setSummary( resume.getSummary() );
        resumeVO.setCertificate( resume.getCertificate() );
        resumeVO.setEducation( resume.getEducation() );
        resumeVO.setDesired_position( resume.getDesired_position() );

        return resumeVO;
    }

    @Override
    public ResumeEducation toResumeEducation(ResumeEducationDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ResumeEducation resumeEducation = new ResumeEducation();

        resumeEducation.setId( dto.getId() );
        resumeEducation.setResume_id( dto.getResume_id() );
        resumeEducation.setSchool_name( dto.getSchool_name() );
        resumeEducation.setDegree( dto.getDegree() );
        resumeEducation.setField_of_study( dto.getField_of_study() );
        resumeEducation.setStart_date( dto.getStart_date() );
        resumeEducation.setEnd_date( dto.getEnd_date() );
        resumeEducation.setCore_courses( dto.getCore_courses() );
        resumeEducation.setAppendix( dto.getAppendix() );

        return resumeEducation;
    }

    @Override
    public ResumeEducationDTO toResumeEducationDTO(ResumeEducation resumeEducation) {
        if ( resumeEducation == null ) {
            return null;
        }

        ResumeEducationDTO resumeEducationDTO = new ResumeEducationDTO();

        resumeEducationDTO.setId( resumeEducation.getId() );
        resumeEducationDTO.setResume_id( resumeEducation.getResume_id() );
        resumeEducationDTO.setSchool_name( resumeEducation.getSchool_name() );
        resumeEducationDTO.setDegree( resumeEducation.getDegree() );
        resumeEducationDTO.setField_of_study( resumeEducation.getField_of_study() );
        resumeEducationDTO.setStart_date( resumeEducation.getStart_date() );
        resumeEducationDTO.setEnd_date( resumeEducation.getEnd_date() );
        resumeEducationDTO.setCore_courses( resumeEducation.getCore_courses() );
        resumeEducationDTO.setAppendix( resumeEducation.getAppendix() );

        return resumeEducationDTO;
    }

    @Override
    public ResumeEducationVO toResumeEducationVO(ResumeEducation resumeEducation) {
        if ( resumeEducation == null ) {
            return null;
        }

        ResumeEducationVO resumeEducationVO = new ResumeEducationVO();

        resumeEducationVO.setId( resumeEducation.getId() );
        resumeEducationVO.setSchool_name( resumeEducation.getSchool_name() );
        resumeEducationVO.setDegree( resumeEducation.getDegree() );
        resumeEducationVO.setField_of_study( resumeEducation.getField_of_study() );
        resumeEducationVO.setStart_date( resumeEducation.getStart_date() );
        resumeEducationVO.setEnd_date( resumeEducation.getEnd_date() );
        resumeEducationVO.setCore_courses( resumeEducation.getCore_courses() );
        resumeEducationVO.setAppendix( resumeEducation.getAppendix() );

        return resumeEducationVO;
    }

    @Override
    public ResumeProject toResumeProject(ResumeProjectDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ResumeProject resumeProject = new ResumeProject();

        resumeProject.setId( dto.getId() );
        resumeProject.setResume_id( dto.getResume_id() );
        resumeProject.setProject_name( dto.getProject_name() );
        resumeProject.setRole( dto.getRole() );
        resumeProject.setStart_date( dto.getStart_date() );
        resumeProject.setEnd_date( dto.getEnd_date() );
        resumeProject.setDescription( dto.getDescription() );

        return resumeProject;
    }

    @Override
    public ResumeProjectDTO toResumeProjectDTO(ResumeProject resumeProject) {
        if ( resumeProject == null ) {
            return null;
        }

        ResumeProjectDTO resumeProjectDTO = new ResumeProjectDTO();

        resumeProjectDTO.setId( resumeProject.getId() );
        resumeProjectDTO.setResume_id( resumeProject.getResume_id() );
        resumeProjectDTO.setProject_name( resumeProject.getProject_name() );
        resumeProjectDTO.setRole( resumeProject.getRole() );
        resumeProjectDTO.setStart_date( resumeProject.getStart_date() );
        resumeProjectDTO.setEnd_date( resumeProject.getEnd_date() );
        resumeProjectDTO.setDescription( resumeProject.getDescription() );

        return resumeProjectDTO;
    }

    @Override
    public ResumeProjectVO toResumeProjectVO(ResumeProject resumeProject) {
        if ( resumeProject == null ) {
            return null;
        }

        ResumeProjectVO resumeProjectVO = new ResumeProjectVO();

        resumeProjectVO.setId( resumeProject.getId() );
        resumeProjectVO.setProject_name( resumeProject.getProject_name() );
        resumeProjectVO.setRole( resumeProject.getRole() );
        resumeProjectVO.setStart_date( resumeProject.getStart_date() );
        resumeProjectVO.setEnd_date( resumeProject.getEnd_date() );
        resumeProjectVO.setDescription( resumeProject.getDescription() );

        return resumeProjectVO;
    }

    @Override
    public ResumeWork toResumeWork(ResumeWorkDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ResumeWork resumeWork = new ResumeWork();

        resumeWork.setId( dto.getId() );
        resumeWork.setResume_id( dto.getResume_id() );
        resumeWork.setCompany_name( dto.getCompany_name() );
        resumeWork.setPosition( dto.getPosition() );
        resumeWork.setStart_date( dto.getStart_date() );
        resumeWork.setEnd_date( dto.getEnd_date() );
        resumeWork.setDescription( dto.getDescription() );

        return resumeWork;
    }

    @Override
    public ResumeWorkDTO toResumeWorkDTO(ResumeWork resumeWork) {
        if ( resumeWork == null ) {
            return null;
        }

        ResumeWorkDTO resumeWorkDTO = new ResumeWorkDTO();

        resumeWorkDTO.setId( resumeWork.getId() );
        resumeWorkDTO.setResume_id( resumeWork.getResume_id() );
        resumeWorkDTO.setCompany_name( resumeWork.getCompany_name() );
        resumeWorkDTO.setPosition( resumeWork.getPosition() );
        resumeWorkDTO.setStart_date( resumeWork.getStart_date() );
        resumeWorkDTO.setEnd_date( resumeWork.getEnd_date() );
        resumeWorkDTO.setDescription( resumeWork.getDescription() );

        return resumeWorkDTO;
    }

    @Override
    public ResumeWorkVO toResumeWorkVO(ResumeWork resumeWork) {
        if ( resumeWork == null ) {
            return null;
        }

        ResumeWorkVO resumeWorkVO = new ResumeWorkVO();

        resumeWorkVO.setId( resumeWork.getId() );
        resumeWorkVO.setCompany_name( resumeWork.getCompany_name() );
        resumeWorkVO.setPosition( resumeWork.getPosition() );
        resumeWorkVO.setStart_date( resumeWork.getStart_date() );
        resumeWorkVO.setEnd_date( resumeWork.getEnd_date() );
        resumeWorkVO.setDescription( resumeWork.getDescription() );

        return resumeWorkVO;
    }

    @Override
    public InterviewVO toInterviewVO(Interview interview) {
        if ( interview == null ) {
            return null;
        }

        InterviewVO interviewVO = new InterviewVO();

        interviewVO.setId( interview.getId() );
        interviewVO.setRating( interview.getRating() );
        interviewVO.setSuggestion( interview.getSuggestion() );
        interviewVO.setCreated_at( interview.getCreated_at() );

        return interviewVO;
    }

    @Override
    public SessionMessageVO toSessionMessageVO(SessionMessage sessionMessage) {
        if ( sessionMessage == null ) {
            return null;
        }

        SessionMessageVO sessionMessageVO = new SessionMessageVO();

        sessionMessageVO.setId( sessionMessage.getId() );
        sessionMessageVO.setRole( sessionMessage.getRole() );
        sessionMessageVO.setMessagetext( sessionMessage.getMessagetext() );
        sessionMessageVO.setSend_date( sessionMessage.getSend_date() );

        return sessionMessageVO;
    }
}
