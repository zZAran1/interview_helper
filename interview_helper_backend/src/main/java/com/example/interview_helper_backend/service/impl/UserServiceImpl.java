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
import com.example.interview_helper_backend.util.LoginRateLimiter;
import com.example.interview_helper_backend.util.UserContext;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {

    private final JwtUtil jwtUtil;
    private final LoginRateLimiter loginRateLimiter;
    BCryptPasswordUtil encoder=new BCryptPasswordUtil();
    //用户不存在时用于等耗时校验的假哈希，防止通过响应时间差枚举邮箱
    private static final String DUMMY_HASH=BCrypt.hashpw("dummy-password",BCrypt.gensalt());

    public UserServiceImpl(JwtUtil jwtUtil, LoginRateLimiter loginRateLimiter) {
        this.jwtUtil = jwtUtil;
        this.loginRateLimiter = loginRateLimiter;
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
    public String login(UserLoginDTO dto, String clientIp) {
        //先查限流，被锁定的请求直接拒绝，不再做数据库查询和BCrypt校验
        loginRateLimiter.checkAllowed(dto.getEmail(), clientIp);
        User dbuser=this.lambdaQuery()
                .eq(User::getEmail,dto.getEmail())
                .one();
        //无论用户是否存在都执行一次BCrypt校验，抹平时间差
        String realPassword=(dbuser==null)?DUMMY_HASH:dbuser.getPassword_hash();
        boolean matched=encoder.checkPassword(dto.getPassword(),realPassword);
        //统一提示，不区分"邮箱未注册"和"密码错误"，防止枚举已注册邮箱
        if(dbuser==null||!matched){
            loginRateLimiter.recordFailure(dto.getEmail(), clientIp);
            throw new LoginException("邮箱或密码错误");
        }
        //登录成功清零失败计数
        loginRateLimiter.recordSuccess(dto.getEmail(), clientIp);
        return jwtUtil.generateToken(String.valueOf(dbuser.getUser_id()), dbuser.getToken_version());
    }
    @Override
    public void logout() {
        //token版本号+1，此前签发的token在下次请求时比对失败，立即全部失效
        this.lambdaUpdate()
                .eq(User::getUser_id, UserContext.getUserId())
                .setSql("token_version = token_version + 1")
                .update();
    }
    @Override
    public int currentTokenVersion(int userId) {
        User user=this.lambdaQuery()
                .eq(User::getUser_id,userId)
                .select(User::getToken_version)
                .one();
        //用户不存在（已注销）返回-1，其token一律拒绝
        return (user==null||user.getToken_version()==null)?-1:user.getToken_version();
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
