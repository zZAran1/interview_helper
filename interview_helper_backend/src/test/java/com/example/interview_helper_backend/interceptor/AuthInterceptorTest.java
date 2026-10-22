package com.example.interview_helper_backend.interceptor;

import com.example.interview_helper_backend.exception.TokenException;
import com.example.interview_helper_backend.service.UserService;
import com.example.interview_helper_backend.util.JwtUtil;
import com.example.interview_helper_backend.util.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthInterceptorTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserService userService;
    @InjectMocks
    private AuthInterceptor authInterceptor;

    @AfterEach
    void tearDown() {
        UserContext.remove();
    }

    private MockHttpServletRequest requestWithToken(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/user/profile");
        if (token != null) {
            request.addHeader("Authorization", token);
        }
        return request;
    }

    @Test
    @DisplayName("OPTIONS预检请求直接放行")
    void optionsRequestAllowed() {
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/api/user/profile");
        assertTrue(authInterceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
        verifyNoInteractions(jwtUtil, userService);
    }

    @Test
    @DisplayName("无token抛TokenException")
    void missingTokenRejected() {
        assertThrows(TokenException.class,
                () -> authInterceptor.preHandle(requestWithToken(null), new MockHttpServletResponse(), new Object()));
    }

    @Test
    @DisplayName("签名无效的token被拒绝")
    void invalidTokenRejected() {
        MockHttpServletRequest request = requestWithToken("Bearer bad");
        when(jwtUtil.validateToken("bad")).thenReturn(false);
        assertThrows(TokenException.class,
                () -> authInterceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    @Test
    @DisplayName("版本号不一致（已登出）的token被拒绝")
    void revokedTokenRejected() {
        MockHttpServletRequest request = requestWithToken("Bearer good");
        when(jwtUtil.validateToken("good")).thenReturn(true);
        when(jwtUtil.getUserId("good")).thenReturn("7");
        when(jwtUtil.getTokenVersion("good")).thenReturn(1);
        when(userService.currentTokenVersion(7)).thenReturn(2);
        assertThrows(TokenException.class,
                () -> authInterceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    @Test
    @DisplayName("已注销用户的旧token被拒绝（防御-1与-1的假匹配）")
    void deletedUserLegacyTokenRejected() {
        //已注销用户库里版本为-1，旧格式token声明也是-1，只做相等比较会误放行，必须单独拒绝
        MockHttpServletRequest request = requestWithToken("Bearer legacy");
        when(jwtUtil.validateToken("legacy")).thenReturn(true);
        when(jwtUtil.getUserId("legacy")).thenReturn("7");
        //库里版本为-1时拦截器直接拒绝，不会再解析token里的版本号
        when(userService.currentTokenVersion(7)).thenReturn(-1);
        assertThrows(TokenException.class,
                () -> authInterceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    @Test
    @DisplayName("有效token放行并注入用户上下文，请求结束后清理")
    void validTokenAccepted() {
        MockHttpServletRequest request = requestWithToken("Bearer good");
        when(jwtUtil.validateToken("good")).thenReturn(true);
        when(jwtUtil.getUserId("good")).thenReturn("7");
        when(jwtUtil.getTokenVersion("good")).thenReturn(2);
        when(userService.currentTokenVersion(7)).thenReturn(2);

        assertTrue(authInterceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
        assertEquals(7, UserContext.getUserId());

        authInterceptor.afterCompletion(request, new MockHttpServletResponse(), new Object(), null);
        assertThrows(TokenException.class, UserContext::getUserId);
    }
}
