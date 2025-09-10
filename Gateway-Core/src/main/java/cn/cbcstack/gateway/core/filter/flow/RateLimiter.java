package cn.cbcstack.gateway.core.filter.flow;

import cn.cbcstack.gateway.core.context.GatewayContext;

public interface RateLimiter {

    void tryConsume(GatewayContext context);

}
