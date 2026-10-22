package com.example.interview_helper_backend.util;

import com.example.interview_helper_backend.exception.LoginException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoginRateLimiterTest {

    private final LoginRateLimiter limiter = new LoginRateLimiter();

    @Test
    @DisplayName("同一邮箱失败5次后被锁定")
    void emailLockedAfterFiveFailures() {
        for (int i = 0; i < 5; i++) {
            limiter.recordFailure("a@test.com", "1.1.1.1");
        }
        assertThrows(LoginException.class, () -> limiter.checkAllowed("a@test.com", "1.1.1.1"));
    }

    @Test
    @DisplayName("登录成功后失败计数清零")
    void successResetsCounters() {
        for (int i = 0; i < 4; i++) {
            limiter.recordFailure("a@test.com", "1.1.1.1");
        }
        limiter.recordSuccess("a@test.com", "1.1.1.1");
        assertDoesNotThrow(() -> limiter.checkAllowed("a@test.com", "1.1.1.1"));
    }

    @Test
    @DisplayName("其他邮箱和其他IP不受锁定影响")
    void otherEmailNotAffected() {
        for (int i = 0; i < 5; i++) {
            limiter.recordFailure("a@test.com", "1.1.1.1");
        }
        assertDoesNotThrow(() -> limiter.checkAllowed("b@test.com", "2.2.2.2"));
    }

    @Test
    @DisplayName("同一IP跨邮箱失败20次后被锁定")
    void ipLockedAfterTwentyFailures() {
        for (int i = 0; i < 20; i++) {
            limiter.recordFailure("user" + i + "@test.com", "3.3.3.3");
        }
        assertThrows(LoginException.class, () -> limiter.checkAllowed("fresh@test.com", "3.3.3.3"));
    }

    @Test
    @DisplayName("大小写与空格不同的同一邮箱共享计数")
    void emailKeyNormalized() {
        for (int i = 0; i < 5; i++) {
            limiter.recordFailure("A@Test.com", "1.1.1.1");
        }
        assertThrows(LoginException.class, () -> limiter.checkAllowed(" a@test.com ", "4.4.4.4"));
    }
}
