package com.example.interview_helper_backend.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("interview")
public class Interview {
    @TableId
    private Integer id;
    private Integer user_id;
    private float rating;
    private String suggestion;
    private LocalDateTime created_at;
}
