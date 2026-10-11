package com.example.interview_helper_backend.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
@Data
@TableName("resume_work")
public class ResumeWork {
    @TableId
    private Integer id;
    private Integer resume_id;//所属简历ID
    private String company_name;
    private String position;
    private LocalDate start_date;
    private LocalDate end_date;
    private String description;
}
