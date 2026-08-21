package com.example.interview_helper_backend.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.interview_helper_backend.converter.Converter;
import com.example.interview_helper_backend.exception.LoginException;
import com.example.interview_helper_backend.exception.RegisterException;
import com.example.interview_helper_backend.exception.UserException;
import com.example.interview_helper_backend.mapper.UserMapper;
import com.example.interview_helper_backend.model.dto.UserLoginDTO;
import com.example.interview_helper_backend.model.dto.UserRegisterDTO;
import com.example.interview_helper_backend.model.vo.UserProfileVO;
import com.example.interview_helper_backend.service.UserService;
import com.example.interview_helper_backend.model.entity.User;
import com.example.interview_helper_backend.util.BCryptPasswordUtil;
import com.example.interview_helper_backend.util.JwtUtil;
import com.example.interview_helper_backend.util.UserContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {

    private final JwtUtil jwtUtil;
    BCryptPasswordUtil encoder=new BCryptPasswordUtil();

    public UserServiceImpl(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void register(UserRegisterDTO dto){//用户注册
        if(this.lambdaQuery().eq(User::getEmail,dto.getEmail()).exists()){//检查邮箱是否已注册
            throw new RegisterException("该邮箱已被注册");
        }
        //哈希密码
        dto.setPassword(encoder.hashPassword(dto.getPassword()));
        User user = Converter.INSTANCE.toUser(dto);
        //获取系统时间
        user.setCreated_at(LocalDateTime.now());
        this.save(user);
    }
    @Override
    public String login(UserLoginDTO dto) {
        User dbuser=this.lambdaQuery()
                .eq(User::getEmail,dto.getEmail())
                .one();
        if(dbuser==null){
            throw new LoginException("该邮箱还未注册");
        }
        String realPassword=dbuser.getPassword_hash();
        if(encoder.checkPassword(dto.getPassword(),realPassword)){
              return jwtUtil.generateToken(String.valueOf(dbuser.getUser_id()));
        }else{
            throw new LoginException("邮箱或密码错误");
        }
    }
    @Override
    public UserProfileVO profile(int user_id){
        User user=this.lambdaQuery()
                .eq(User::getUser_id,user_id)
                .select(User::getUsername,User::getEmail,User::getCreated_at)
                .one();
        if(user==null){
            throw new UserException("用户还未登录");
        }
        return Converter.INSTANCE.toVO(user);
    }
    @Override
    public void deleteUser(){
        int user_id= UserContext.getUserId();
        this.lambdaUpdate()
                .eq(User::getUser_id,user_id)
                .remove();
    }
}
