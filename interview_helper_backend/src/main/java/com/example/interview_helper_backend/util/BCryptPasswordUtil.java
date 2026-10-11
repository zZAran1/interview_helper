package com.example.interview_helper_backend.util;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptPasswordUtil {
    //哈希密码
    public  String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    //验证密码
    public  boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
