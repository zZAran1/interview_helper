package com.example.interview_helper_backend.util;

import com.example.interview_helper_backend.exception.TokenException;

public class UserContext {
    private  static final ThreadLocal<Integer> USER_ID=new ThreadLocal<>();

    public static void setUserId(int userId){
        USER_ID.set(userId);
    }
    public static int getUserId(){
        Integer userId = USER_ID.get();
        if(userId==null){
            throw new TokenException("用户未登录，请先登录");
        }
        return userId;
    }
    public static void remove(){
        USER_ID.remove();
    }
}
