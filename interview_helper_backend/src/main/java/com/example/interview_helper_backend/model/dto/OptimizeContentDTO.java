package com.example.interview_helper_backend.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OptimizeContentDTO {
    @Size(max = 2000, message = "个人简介不能超过2000字")
    private String summary;//个人简介
    @Size(max = 1000, message = "在校经历不能超过1000字")
    private String education_appendix;//教育经历补充
    @Size(max = 2000, message = "项目描述不能超过2000字")
    private String project_description;//项目描述
    @Size(max = 2000, message = "工作描述不能超过2000字")
    private String work_description;//工作描述
}
