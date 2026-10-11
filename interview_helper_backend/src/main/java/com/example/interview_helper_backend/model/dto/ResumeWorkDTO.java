package com.example.interview_helper_backend.model.dto;

import lombok.Data;

import java.time.LocalDate;
@Data
public class ResumeWorkDTO {
    private Integer id;
    private Integer resume_id;//所属简历ID
    private String company_name;
    private String position;
    private LocalDate start_date;
    private LocalDate end_date;
    private String description;
}
