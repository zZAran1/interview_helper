package com.example.interview_helper_backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//为所有响应统一附加安全头，减少浏览器端的常见攻击面
@Component
public class SecurityHeadersFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //禁止浏览器对响应内容做MIME嗅探
        response.setHeader("X-Content-Type-Options", "nosniff");
        //禁止页面被嵌入iframe，防点击劫持
        response.setHeader("X-Frame-Options", "DENY");
        //接口响应含个人数据，禁止中间层和浏览器缓存
        response.setHeader("Cache-Control", "no-store");
        //不向外泄露来源页面
        response.setHeader("Referrer-Policy", "no-referrer");
        //HSTS 只在 HTTPS 下生效，HTTP 请求不下发避免误导
        if (request.isSecure()) {
            response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        }
        filterChain.doFilter(request, response);
    }
}
