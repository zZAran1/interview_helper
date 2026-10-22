package com.example.interview_helper_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.interview_helper_backend.model.entity.User;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserMapper extends BaseMapper<User> {

}
