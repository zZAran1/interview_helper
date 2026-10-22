package com.example.interview_helper_backend.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
@Data
public class ResumeEducationDTO {
    private Integer id;//更新/删除时必传，创建时不传
    private Integer resume_id;//所属简历ID（服务端从登录态取，不信任前端）
    @Size(max = 100, message = "学校名称不能超过100字")
    private String school_name;//学校名称
    @Size(max = 50, message = "学历层次不能超过50字")
    private String degree;//学历层次
    @Size(max = 100, message = "专业不能超过100字")
    private String field_of_study;//专业
    private LocalDate start_date;//开始时间
    private LocalDate end_date;//结束时间
    @Size(max = 100, message = "主修课程不能超过100字")
    private String core_courses;//主修课程
    @Size(max = 1000, message = "在校经历不能超过1000字")
    private String appendix;//在校经历
}
