package com.example.interview_helper_backend.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;



@Data
@TableName("resume")
public class Resume  {
    @TableId
    private Integer id;//简历id
    private Integer user_id;//所属用户id
    private String real_name;//真实姓名
    private int age;//年龄
    private String gender;//性别
    private String phone_number;//电话号码
    private String skill;//技能
    private String summary;//个人简介
    private String certificate;//证书
    private String education;//学历
    private String desired_position;//期望职位
}
