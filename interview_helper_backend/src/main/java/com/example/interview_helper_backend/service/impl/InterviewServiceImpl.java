package com.example.interview_helper_backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.interview_helper_backend.converter.Converter;
import com.example.interview_helper_backend.exception.InterviewException;
import com.example.interview_helper_backend.mapper.InterviewMapper;
import com.example.interview_helper_backend.model.dto.InterviewDTO;
import com.example.interview_helper_backend.model.dto.ResumeAllDTO;
import com.example.interview_helper_backend.model.dto.ResumeEducationDTO;
import com.example.interview_helper_backend.model.dto.ResumeDTO;
import com.example.interview_helper_backend.model.dto.ResumeProjectDTO;
import com.example.interview_helper_backend.model.dto.ResumeWorkDTO;
import com.example.interview_helper_backend.model.dto.SessionMessageDTO;
import com.example.interview_helper_backend.model.entity.Interview;
import com.example.interview_helper_backend.model.vo.InterviewVO;
import com.example.interview_helper_backend.model.vo.SessionMessageVO;
import com.example.interview_helper_backend.service.InterviewService;
import com.example.interview_helper_backend.service.ResumeService;
import com.example.interview_helper_backend.service.SessionMessageService;
import com.example.interview_helper_backend.util.UserContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class InterviewServiceImpl extends ServiceImpl<InterviewMapper, Interview> implements InterviewService {
    private static final Pattern RATING_PATTERN = Pattern.compile("评分[：:]\\s*([0-9]+(?:\\.[0-9]+)?)");

    private final SessionMessageService sessionMessageService;
    private final ResumeService resumeService;

    public InterviewServiceImpl(SessionMessageService sessionMessageService, ResumeService resumeService) {
        this.sessionMessageService = sessionMessageService;
        this.resumeService = resumeService;
    }

    @Override
    public InterviewVO startInterview() {
        int userId = UserContext.getUserId();
        //先生成第一题再落库：AI失败时不会留下没有题目的空面试记录
        String firstQuestion = sessionMessageService.getFirstQuestion(buildResumeContext());
        if (firstQuestion == null || firstQuestion.isBlank()) {
            //兜底：AI返回空题目时给一个默认开场问题，保证面试流程能继续
            firstQuestion = "你好，请先做一个简短的自我介绍吧。";
        }

        Interview interview = new Interview();
        interview.setUser_id(userId);
        interview.setCreated_at(LocalDateTime.now());
        this.save(interview);
        sessionMessageService.saveAiMessage(interview.getId(), firstQuestion);

        InterviewVO vo = Converter.INSTANCE.toInterviewVO(interview);
        vo.setFirst_question(firstQuestion);
        return vo;
    }

    @Override
    public List<InterviewVO> viewInterview() {
        int userId = UserContext.getUserId();
        List<Interview> interviews = lambdaQuery()
                .eq(Interview::getUser_id, userId)
                .orderByDesc(Interview::getId)
                .list();
        return interviews.stream()
                .map(Converter.INSTANCE::toInterviewVO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteInterview(InterviewDTO dto) {
        Interview interview = getOwnedInterview(dto.getId());
        //session_message外键无级联删除，需先删除消息
        sessionMessageService.deleteByInterviewId(interview.getId());
        this.removeById(interview.getId());
    }

    @Override
    public InterviewVO endInterview(InterviewDTO dto) {
        Interview interview = getOwnedInterview(dto.getId());
        String conversation = sessionMessageService.buildConversationText(interview.getId());
        if (conversation.isBlank()) {
            throw new InterviewException("该面试暂无对话记录，无法评估");
        }
        String evaluation = sessionMessageService.evaluate(conversation);
        interview.setRating(parseRating(evaluation));
        interview.setSuggestion(parseSuggestion(evaluation));
        this.updateById(interview);
        return Converter.INSTANCE.toInterviewVO(interview);
    }

    @Override
    public List<SessionMessageVO> sendMessage(SessionMessageDTO dto) {
        getOwnedInterview(dto.getInterview_id());
        sessionMessageService.reply(dto.getInterview_id(), dto.getMessagetext());
        return toMessageVOs(dto.getInterview_id());
    }

    @Override
    public List<SessionMessageVO> viewMessages(Integer interviewId) {
        getOwnedInterview(interviewId);
        return toMessageVOs(interviewId);
    }

    private List<SessionMessageVO> toMessageVOs(Integer interviewId) {
        return sessionMessageService.listMessages(interviewId).stream()
                .map(Converter.INSTANCE::toSessionMessageVO)
                .collect(Collectors.toList());
    }

    private Interview getOwnedInterview(Integer id) {
        if (id == null) {
            throw new InterviewException("面试ID不能为空");
        }
        Interview interview = this.getById(id);
        if (interview == null) {
            throw new InterviewException("面试不存在");
        }
        int userId = UserContext.getUserId();
        if (!Integer.valueOf(userId).equals(interview.getUser_id())) {
            throw new InterviewException("无权访问该面试");
        }
        return interview;
    }

    private String buildResumeContext() {
        ResumeAllDTO all = resumeService.getResumeAll();
        if (all == null || all.getResumeDTO() == null) {
            return "";
        }
        ResumeDTO resume = all.getResumeDTO();
        StringBuilder context = new StringBuilder();
        appendIfPresent(context, "求职意向", resume.getDesired_position());
        appendIfPresent(context, "技能", resume.getSkill());
        appendIfPresent(context, "个人简介", resume.getSummary());
        appendIfPresent(context, "学历", resume.getEducation());
        if (all.getResumeEducationDTO() != null) {
            appendIfPresent(context, "教育经历", formatEducation(all.getResumeEducationDTO()));
        }
        if (all.getResumeProjectDTO() != null) {
            appendIfPresent(context, "项目经历", formatProject(all.getResumeProjectDTO()));
        }
        if (all.getResumeWorkDTO() != null) {
            appendIfPresent(context, "工作经历", formatWork(all.getResumeWorkDTO()));
        }
        return context.toString();
    }

    private String formatEducation(ResumeEducationDTO education) {
        StringBuilder text = new StringBuilder();
        appendSegment(text, education.getSchool_name());
        appendSegment(text, education.getDegree());
        appendSegment(text, education.getField_of_study());
        if (education.getCore_courses() != null && !education.getCore_courses().isBlank()) {
            text.append("主修课程：").append(education.getCore_courses()).append("；");
        }
        if (education.getAppendix() != null && !education.getAppendix().isBlank()) {
            text.append("在校经历：").append(education.getAppendix());
        }
        return text.toString();
    }

    private String formatProject(ResumeProjectDTO project) {
        StringBuilder text = new StringBuilder();
        if (project.getProject_name() != null && !project.getProject_name().isBlank()) {
            text.append("项目名称：").append(project.getProject_name()).append("；");
        }
        if (project.getRole() != null && !project.getRole().isBlank()) {
            text.append("担任角色：").append(project.getRole()).append("；");
        }
        if (project.getDescription() != null && !project.getDescription().isBlank()) {
            text.append("项目描述：").append(project.getDescription());
        }
        return text.toString();
    }

    private String formatWork(ResumeWorkDTO work) {
        StringBuilder text = new StringBuilder();
        if (work.getCompany_name() != null && !work.getCompany_name().isBlank()) {
            text.append("公司：").append(work.getCompany_name()).append("；");
        }
        if (work.getPosition() != null && !work.getPosition().isBlank()) {
            text.append("职位：").append(work.getPosition()).append("；");
        }
        if (work.getDescription() != null && !work.getDescription().isBlank()) {
            text.append("工作内容：").append(work.getDescription());
        }
        return text.toString();
    }

    private void appendSegment(StringBuilder text, String value) {
        if (value != null && !value.isBlank()) {
            text.append(value).append("，");
        }
    }

    private void appendIfPresent(StringBuilder context, String label, String value) {
        if (value != null && !value.isBlank()) {
            context.append(label).append("：").append(value).append("\n");
        }
    }

    private float parseRating(String evaluation) {
        Matcher matcher = RATING_PATTERN.matcher(evaluation);
        if (!matcher.find()) {
            throw new InterviewException("AI评估结果异常，请重试");
        }
        float rating = Float.parseFloat(matcher.group(1));
        return Math.max(0f, Math.min(10f, rating));
    }

    private String parseSuggestion(String evaluation) {
        int index = evaluation.indexOf("建议");
        if (index >= 0) {
            return evaluation.substring(index + 2).replaceFirst("^[：:\\s]+", "").trim();
        }
        return evaluation.replaceAll("评分[：:]\\s*[0-9]+(?:\\.[0-9]+)?", "").trim();
    }
}
