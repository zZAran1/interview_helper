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
    //包装类型：null表示尚未评估，与0分（评估过但得0分）区分开
    private Float rating;
    private String suggestion;
        //状态：0进行中 1已结束
    public static final int STATUS_ONGOING = 0;
    public static final int STATUS_ENDED = 1;
    private Integer status;
    private LocalDateTime created_at;
}
