package com.example.interview_helper_backend.interceptor;

import com.example.interview_helper_backend.exception.TokenException;
import com.example.interview_helper_backend.service.UserService;
import com.example.interview_helper_backend.util.JwtUtil;
import com.example.interview_helper_backend.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if("OPTIONS".equalsIgnoreCase(request.getMethod())){
            return true;
        }
        String token = request.getHeader("Authorization");
        if(token==null||token.isEmpty()) {
            throw new TokenException("还未登录");
        }
        if(token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if(token.isEmpty()) {
            throw new TokenException("还未登录");
        }
        if(!jwtUtil.validateToken(token)) {
            throw new TokenException("登录失效,请重新登录");
        }
        int user_id= Integer.parseInt(jwtUtil.getUserId(token));
        //token版本号校验：版本不一致、用户已注销都拒绝
        //注意：已注销用户版本为-1，旧格式token无版本声明也是-1，不能只做相等比较，必须单独拒绝-1
        int dbVersion=userService.currentTokenVersion(user_id);
        if(dbVersion<0||jwtUtil.getTokenVersion(token)!=dbVersion) {
            throw new TokenException("登录失效,请重新登录");
        }
        UserContext.setUserId(user_id);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.remove();
    }
}
