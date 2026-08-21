package com.example.interview_helper_backend.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserProfileVO {
    private String username;
    private String email;
    private LocalDateTime created_at;
}
