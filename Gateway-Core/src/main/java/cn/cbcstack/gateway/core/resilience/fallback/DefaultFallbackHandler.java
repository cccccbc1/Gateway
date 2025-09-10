package cn.cbcstack.gateway.core.resilience.fallback;


import cn.cbcstack.gateway.common.enums.ResponseCode;
import cn.cbcstack.gateway.core.context.GatewayContext;
import cn.cbcstack.gateway.core.helper.ContextHelper;
import cn.cbcstack.gateway.core.helper.ResponseHelper;

import static cn.cbcstack.gateway.common.constant.FallbackConstant.DEFAULT_FALLBACK_HANDLER_NAME;

public class DefaultFallbackHandler implements FallbackHandler {

    @Override
    public void handle(Throwable throwable, GatewayContext context) {
        context.setThrowable(throwable);
        context.setResponse(ResponseHelper.buildGatewayResponse(ResponseCode.GATEWAY_FALLBACK));
        ContextHelper.writeBackResponse(context);
    }

    @Override
    public String mark() {
        return DEFAULT_FALLBACK_HANDLER_NAME;
    }

}
