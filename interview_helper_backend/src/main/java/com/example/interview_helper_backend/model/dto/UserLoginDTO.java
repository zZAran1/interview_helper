package com.example.interview_helper_backend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginDTO {
    @NotBlank(message="邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱不能超过100字")
    private String email;
    @NotBlank(message = "密码不能为空")
    @Size(max = 64, message = "密码长度不合法")
    private String password;
}
