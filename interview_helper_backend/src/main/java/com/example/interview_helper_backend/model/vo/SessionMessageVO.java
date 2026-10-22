package com.example.interview_helper_backend.model.vo;

import com.example.interview_helper_backend.model.entity.SessionMessage;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class SessionMessageVO {
    private Integer id;
    private SessionMessage.Role role;
    private String messagetext;
    private LocalDateTime send_date;
}
