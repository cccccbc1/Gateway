package cn.cbcstack.gateway.core.algorithm;

import cn.cbcstack.gateway.common.enums.ResponseCode;
import cn.cbcstack.gateway.common.exception.LimitedException;
import cn.cbcstack.gateway.core.context.GatewayContext;
import cn.cbcstack.gateway.core.filter.flow.RateLimiter;

import java.time.Instant;
import java.util.Deque;
import java.util.LinkedList;

/**
 * 华东窗口限流
 */
public class SlidingWindowRateLimiter implements RateLimiter {

    private final int capacity; // 最大允许请求数
    private final int windowSizeInMillis; // 窗口大小，单位：毫秒
    private final Deque<Long> requestTimestamps; // 存储每个请求的时间戳

    public SlidingWindowRateLimiter(int capacity, int windowSizeInMillis) {
        this.capacity = capacity;
        this.windowSizeInMillis = windowSizeInMillis;
        this.requestTimestamps = new LinkedList<>();
    }

    @Override
    public synchronized void tryConsume(GatewayContext context) {
        long now = Instant.now().toEpochMilli();
        cleanOldRequests(now);
        if (requestTimestamps.size() < capacity) {
            requestTimestamps.addLast(now);
            context.doFilter();
        } else {
            throw new LimitedException(ResponseCode.TOO_MANY_REQUESTS);
        }
    }

    private void cleanOldRequests(long currentTime) {
        while (!requestTimestamps.isEmpty() && (currentTime - requestTimestamps.peekFirst()) > windowSizeInMillis) {
            requestTimestamps.pollFirst();
        }
    }

}
