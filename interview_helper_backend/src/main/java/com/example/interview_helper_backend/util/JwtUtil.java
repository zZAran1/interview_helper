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
    //生成token（携带token版本号，供服务端吊销校验）
    public String generateToken(String userId, int tokenVersion) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("token_version", tokenVersion)
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
    //获取token版本号；旧格式token无该声明，返回-1（与库中版本永不相等，视为失效）
    public int getTokenVersion(String token) {
        Integer version = parseToken(token).get("token_version", Integer.class);
        return version == null ? -1 : version;
    }
}
