package com.example.interview_helper_backend.model.vo;

import lombok.Data;

import java.time.LocalDate;
@Data
public class ResumeWorkVO {
    private Integer id;
    private String company_name;
    private String position;
    private LocalDate start_date;
    private LocalDate end_date;
    private String description;
}
