package com.example.interview_helper_backend.model.vo;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewVO {
    private Integer id;
    private float rating;
    private String suggestion;
    private LocalDateTime created_at;
    private String first_question;//仅开始面试时返回的首个AI问题
}
