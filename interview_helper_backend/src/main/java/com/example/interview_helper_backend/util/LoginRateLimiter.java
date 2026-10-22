package com.example.interview_helper_backend.util;

import com.example.interview_helper_backend.exception.LoginException;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

//登录失败限流：按"邮箱"和"来源IP"两个维度计数，超限直接拒绝，防止密码暴力破解
//注意：计数只存在本机内存，重启即清零；多实例部署时应换成 Redis 等共享存储
@Component
public class LoginRateLimiter {
    //同一邮箱10分钟内最多失败5次
    private static final int MAX_FAILURES_PER_EMAIL = 5;
    //同一IP上限放宽，避免NAT出口（多人共享一个公网IP）被误伤
    private static final int MAX_FAILURES_PER_IP = 20;
    private static final long WINDOW_MS = 10 * 60 * 1000L;

    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    //校验是否已被锁定，超限抛业务异常
    public void checkAllowed(String email, String ip) {
        purgeExpired();
        check(windows.get(emailKey(email)), MAX_FAILURES_PER_EMAIL);
        check(windows.get(ipKey(ip)), MAX_FAILURES_PER_IP);
    }

    //登录失败时计数
    public void recordFailure(String email, String ip) {
        recordFailureKey(emailKey(email));
        recordFailureKey(ipKey(ip));
    }

    //登录成功时清零，正常用户不会被历史失败拖累
    public void recordSuccess(String email, String ip) {
        windows.remove(emailKey(email));
        windows.remove(ipKey(ip));
    }

    private void check(Window w, int maxFailures) {
        if (w == null) {
            return;
        }
        if (w.count >= maxFailures && System.currentTimeMillis() - w.windowStart < WINDOW_MS) {
            throw new LoginException("登录尝试次数过多，请10分钟后再试");
        }
    }

    private synchronized void recordFailureKey(String key) {
        long now = System.currentTimeMillis();
        windows.compute(key, (k, w) ->
                (w == null || now - w.windowStart > WINDOW_MS) ? new Window(now, 1) : new Window(w.windowStart, w.count + 1));
    }

    //惰性清理过期窗口，防止长期运行后内存膨胀
    private synchronized void purgeExpired() {
        long now = System.currentTimeMillis();
        windows.entrySet().removeIf(e -> now - e.getValue().windowStart > WINDOW_MS);
    }

    private String emailKey(String email) {
        return "email:" + email.trim().toLowerCase();
    }

    private String ipKey(String ip) {
        return "ip:" + ip;
    }

    private static class Window {
        final long windowStart;
        final int count;

        Window(long windowStart, int count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}
