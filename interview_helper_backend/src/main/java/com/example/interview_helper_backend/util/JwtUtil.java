package com.example.interview_helper_backend.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String base64Secret;
    @Value("${jwt.expiration}")
    private long expirationTime;
    private SecretKey getSigningKey() {
        // 1. 解码Base64
        byte[] keyBytes = Base64.getDecoder().decode(base64Secret);
        // 2. 创建HMAC SHA-256密钥
        return Keys.hmacShaKeyFor(keyBytes);
    }
    //生成token
    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }
    //解析token
    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    //校验
    public boolean validateToken(String token){
        try{
            parseToken(token);
            return true;
        }catch (Exception e) {
            return false;
        }
    }
    //获取userId
    public String getUserId(String token) {
        return parseToken(token).getSubject();
    }
}
