package cn.cbcstack.gateway.core.resilience.fallback;

import cn.cbcstack.gateway.core.context.GatewayContext;

public interface FallbackHandler {

    void handle(Throwable throwable, GatewayContext context);

    String mark();

}
