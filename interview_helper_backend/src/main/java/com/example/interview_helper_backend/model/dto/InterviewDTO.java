package com.example.interview_helper_backend.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InterviewDTO {
    @NotNull(message = "面试ID不能为空")
    private Integer id;
}
