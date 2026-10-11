package com.example.interview_helper_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.interview_helper_backend.model.dto.UserLoginDTO;
import com.example.interview_helper_backend.model.dto.UserRegisterDTO;
import com.example.interview_helper_backend.model.entity.User;
import com.example.interview_helper_backend.model.vo.UserProfileVO;

public interface UserService extends IService<User> {
    void register(UserRegisterDTO dto);
    String login(UserLoginDTO dto);
    UserProfileVO profile(int user_id);
    void deleteUser();
}
