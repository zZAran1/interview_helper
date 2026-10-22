package com.example.interview_helper_backend.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
@Data
public class ResumeProjectDTO {
    private Integer id;//更新/删除时必传，创建时不传
    private Integer resume_id;//所属简历ID（服务端从登录态取，不信任前端）
    @Size(max = 100, message = "项目名称不能超过100字")
    private String project_name;//项目名称
    @Size(max = 50, message = "担任角色不能超过50字")
    private String role;//在项目中充当的角色
    private LocalDate start_date;//项目开始时间
    private LocalDate end_date;//项目于完工时间
    @Size(max = 2000, message = "项目描述不能超过2000字")
    private String description;//项目描述
}
