package com.example.interview_helper_backend.model.dto;

import lombok.Data;

import java.time.LocalDate;
@Data
public class ResumeProjectDTO {
    private Integer id;
    private Integer resume_id;//所属简历ID
    private String project_name;//项目名称
    private String role;//在项目中充当的角色
    private LocalDate start_date;//项目开始时间
    private LocalDate end_date;//项目于完工时间
    private String description;//项目描述
}
