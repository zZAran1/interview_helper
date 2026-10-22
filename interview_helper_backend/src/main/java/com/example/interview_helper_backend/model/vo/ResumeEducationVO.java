package com.example.interview_helper_backend.model.vo;
import lombok.Data;

import java.time.LocalDate;
@Data
public class ResumeEducationVO {
    private Integer id;
    private String school_name;//学校名称
    private String degree;//学历层次
    private String field_of_study;//专业
    private LocalDate start_date;//开始时间
    private LocalDate end_date;//结束时间
    private String core_courses;//主修课程
    private String appendix;//在校经历
}
