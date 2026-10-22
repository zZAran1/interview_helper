package com.example.interview_helper_backend.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 面试场次级锁：同一场面试的发消息/结束评估串行执行，防止并发乱序与竞态。
 * 仅 JVM 内有效，多实例部署时需换 Redis 分布式锁。
 */
public final class InterviewLocks {

    private static final ConcurrentHashMap<Integer, ReentrantLock> LOCKS = new ConcurrentHashMap<>();

    private InterviewLocks() {
    }

    public static ReentrantLock getLock(Integer interviewId) {
        return LOCKS.computeIfAbsent(interviewId, k -> new ReentrantLock());
    }
}