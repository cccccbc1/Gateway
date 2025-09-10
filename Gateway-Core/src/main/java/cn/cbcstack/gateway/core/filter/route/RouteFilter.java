package cn.cbcstack.gateway.core.filter.route;

import cn.cbcstack.gateway.common.enums.ResponseCode;
import cn.cbcstack.gateway.config.pojo.RouteDefinition;
import cn.cbcstack.gateway.core.context.GatewayContext;
import cn.cbcstack.gateway.core.filter.Filter;
import cn.cbcstack.gateway.core.helper.ContextHelper;
import cn.cbcstack.gateway.core.helper.ResponseHelper;
import cn.cbcstack.gateway.core.resilience.Resilience;
import org.asynchttpclient.Response;

import java.util.concurrent.CompletableFuture;

import static cn.cbcstack.gateway.common.constant.FilterConstant.ROUTE_FILTER_NAME;
import static cn.cbcstack.gateway.common.constant.FilterConstant.ROUTE_FILTER_ORDER;

/**
 * 弹性路由过滤器
 */
public class RouteFilter implements Filter {

    @Override
    public void doPreFilter(GatewayContext context) {
        RouteDefinition.ResilienceConfig resilience = context.getRoute().getResilience();
        if (resilience.isEnabled()) { // 开启弹性配置
            Resilience.getInstance().executeRequest(context);
        } else {
            CompletableFuture<Response> future = RouteUtil.buildRouteSupplier(context).get().toCompletableFuture();
            future.exceptionally(throwable -> {
                context.setResponse(ResponseHelper.buildGatewayResponse(ResponseCode.HTTP_RESPONSE_ERROR));
                ContextHelper.writeBackResponse(context);
                return null;
            });
        }
    }

    @Override
    public void doPostFilter(GatewayContext context) {
        context.doFilter();
    }

    @Override
    public String mark() {
        return ROUTE_FILTER_NAME;
    }

    @Override
    public int getOrder() {
        return ROUTE_FILTER_ORDER;
    }

}
