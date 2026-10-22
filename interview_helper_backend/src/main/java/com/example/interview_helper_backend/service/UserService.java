package com.example.interview_helper_backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.interview_helper_backend.model.dto.UserLoginDTO;
import com.example.interview_helper_backend.model.dto.UserRegisterDTO;
import com.example.interview_helper_backend.model.entity.User;
import com.example.interview_helper_backend.model.vo.UserProfileVO;

public interface UserService extends IService<User> {
    void register(UserRegisterDTO dto);
    String login(UserLoginDTO dto, String clientIp);
    //登出：token版本号+1，此前签发的token全部失效
    void logout();
    //查询用户当前token版本号；用户不存在（已注销）返回-1
    int currentTokenVersion(int userId);
    UserProfileVO profile(int user_id);
    void deleteUser();
}
