package com.example.interview_helper_backend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@TableName("users")
public class User {
    @TableId
    private Integer user_id;//用户id
    private String username;//用户名
    private String password_hash;//哈希密码
    private String email;//邮箱
    private LocalDateTime created_at;//创建时间
    private Integer token_version;//token版本号，登出/注销时+1使旧token全部失效
}
