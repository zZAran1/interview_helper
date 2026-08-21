package com.example.interview_helper_backend.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@TableName("session_message")
public class SessionMessage {
    @TableId
    private Integer id;
    private Integer interview_id;
    public enum Role{
        ai,user
    }
    private Role role;
    private String messagetext;
    private LocalDateTime send_date;
}
