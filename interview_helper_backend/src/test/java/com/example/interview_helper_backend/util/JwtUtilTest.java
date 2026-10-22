package com.example.interview_helper_backend.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String BASE64_SECRET = Base64.getEncoder()
            .encodeToString("01234567890123456789012345678901".getBytes());

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "base64Secret", BASE64_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expirationTime", 60_000L);
    }

    @Test
    @DisplayName("正常token能解析出userId和版本号")
    void normalTokenRoundTrip() {
        String token = jwtUtil.generateToken("42", 3);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals("42", jwtUtil.getUserId(token));
        assertEquals(3, jwtUtil.getTokenVersion(token));
    }

    @Test
    @DisplayName("被篡改的token校验失败")
    void tamperedTokenRejected() {
        String token = jwtUtil.generateToken("42", 1);
        String tampered = token.substring(0, token.length() - 2) + "ab";
        assertFalse(jwtUtil.validateToken(tampered));
    }

    @Test
    @DisplayName("过期token校验失败")
    void expiredTokenRejected() {
        ReflectionTestUtils.setField(jwtUtil, "expirationTime", -1000L);
        String token = jwtUtil.generateToken("42", 1);
        assertFalse(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("用别的secret签发的token校验失败")
    void tokenSignedWithOtherSecretRejected() {
        JwtUtil other = new JwtUtil();
        ReflectionTestUtils.setField(other, "base64Secret",
                Base64.getEncoder().encodeToString("99999999999999999999999999999999".getBytes()));
        ReflectionTestUtils.setField(other, "expirationTime", 60_000L);
        String token = other.generateToken("42", 1);
        assertFalse(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("旧格式token无版本号声明时返回-1")
    void legacyTokenWithoutVersionReturnsMinusOne() {
        //手工签发一个不带 token_version 声明的旧格式token，模拟升级前遗留的token
        String oldToken = Jwts.builder()
                .setSubject("42")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(BASE64_SECRET)))
                .compact();
        assertTrue(jwtUtil.validateToken(oldToken));
        assertEquals(-1, jwtUtil.getTokenVersion(oldToken));
    }
}
