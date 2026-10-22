package com.example.interview_helper_backend.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("resume_education")
public class ResumeEducation {
    @TableId
    private Integer id;//教育经历ID
    private Integer resume_id;//所属简历ID
    private String school_name;//学校名称
    private String degree;//学历层次
    private String field_of_study;//专业
    private LocalDate start_date;//开始时间
    private LocalDate end_date;//结束时间
    private String core_courses;//主修课程
    private String appendix;//在校经历
}
