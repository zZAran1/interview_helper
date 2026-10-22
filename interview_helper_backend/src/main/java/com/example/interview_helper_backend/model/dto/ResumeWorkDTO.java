package com.example.interview_helper_backend.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
@Data
public class ResumeWorkDTO {
    private Integer id;//更新/删除时必传，创建时不传
    private Integer resume_id;//所属简历ID（服务端从登录态取，不信任前端）
    @Size(max = 100, message = "公司名称不能超过100字")
    private String company_name;
    @Size(max = 50, message = "职位不能超过50字")
    private String position;
    private LocalDate start_date;
    private LocalDate end_date;
    @Size(max = 2000, message = "工作描述不能超过2000字")
    private String description;
}
