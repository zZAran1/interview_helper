package com.example.interview_helper_backend.model.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResumeDTO {
    @Size(max = 50, message = "姓名不能超过50字")
    private String real_name;//真实姓名
    @Min(value = 0, message = "年龄不能为负数")
    @Max(value = 150, message = "年龄不合法")
    private int age;//年龄
    @Size(max = 1, message = "性别仅1个字符")
    private String gender;//性别
    @Size(max = 11, message = "手机号不能超过11位")
    private String phone_number;//电话号码
    @Size(max = 150, message = "技能不能超过150字")
    private String skill;//技能
    @Size(max = 2000, message = "个人简介不能超过2000字")
    private String summary;//个人简介
    @Size(max = 200, message = "证书不能超过200字")
    private String certificate;//证书
    @Size(max = 20, message = "学历不能超过20字")
    private String education;//学历
    @Size(max = 100, message = "期望职位不能超过100字")
    private String desired_position;//期望职位
}
