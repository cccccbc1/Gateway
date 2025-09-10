package cn.cbcstack.gateway.core.filter.cors;

import cn.cbcstack.gateway.common.enums.ResponseCode;
import cn.cbcstack.gateway.core.context.GatewayContext;
import cn.cbcstack.gateway.core.dto.response.GatewayResponse;
import cn.cbcstack.gateway.core.filter.Filter;
import cn.cbcstack.gateway.core.helper.ContextHelper;
import cn.cbcstack.gateway.core.helper.ResponseHelper;
import io.netty.handler.codec.http.HttpMethod;

import static cn.cbcstack.gateway.common.constant.FilterConstant.CORS_FILTER_NAME;
import static cn.cbcstack.gateway.common.constant.FilterConstant.CORS_FILTER_ORDER;

/**
 * 跨域过滤器
 */
public class CorsFilter implements Filter {

    @Override
    public void doPreFilter(GatewayContext context) {
        if (HttpMethod.OPTIONS.equals(context.getRequest().getMethod())) {
            context.setResponse(ResponseHelper.buildGatewayResponse(ResponseCode.SUCCESS));
            ContextHelper.writeBackResponse(context);
        } else {
            context.doFilter();
        }
    }

    @Override
    public void doPostFilter(GatewayContext context) {
        GatewayResponse gatewayResponse = context.getResponse();
        gatewayResponse.addHeader("Access-Control-Allow-Origin", "*");
        gatewayResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        gatewayResponse.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        gatewayResponse.addHeader("Access-Control-Allow-Credentials", "true");
        context.doFilter();
    }

    @Override
    public String mark() {
        return CORS_FILTER_NAME;
    }

    @Override
    public int getOrder() {
        return CORS_FILTER_ORDER;
    }

}
