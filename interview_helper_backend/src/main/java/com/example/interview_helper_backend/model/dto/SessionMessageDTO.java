package com.example.interview_helper_backend.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SessionMessageDTO {
    @NotNull(message = "面试ID不能为空")
    private Integer interview_id;
    @NotBlank(message = "回答内容不能为空")
    @Size(max = 1000, message = "回答内容不能超过1000字")
    private String messagetext;
}
