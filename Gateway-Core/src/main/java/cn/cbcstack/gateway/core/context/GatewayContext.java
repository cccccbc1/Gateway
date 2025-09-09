package cn.cbcstack.gateway.core.context;

import cn.cbcstack.gateway.config.pojo.RouteDefinition;
import cn.cbcstack.gateway.core.dto.request.GatewayRequest;
import cn.cbcstack.gateway.core.dto.response.GatewayResponse;
import cn.cbcstack.gateway.core.filter.FilterChain;
import cn.cbcstack.gateway.core.helper.ContextHelper;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

@Data
public class GatewayContext {

    /**
     * Netty上下文
     */
    private ChannelHandlerContext nettyCtx;

    /**
     * 请求过程中发生的异常
     */
    private Throwable throwable;

    private GatewayRequest request;

    private GatewayResponse response;

    private RouteDefinition route;

    private boolean keepAlive;

    private FilterChain filterChain;

    private int curFilterIndex = 0;
    private boolean isDoPreFilter = true;

    public GatewayContext(ChannelHandlerContext nettyCtx, GatewayRequest request,
                          RouteDefinition route, boolean keepAlive) {
        this.nettyCtx = nettyCtx;
        this.request = request;
        this.route = route;
        this.keepAlive = keepAlive;
    }

    public void doFilter() {
        int size = filterChain.size();
        if (isDoPreFilter) {
            filterChain.doPreFilter(curFilterIndex++, this);
            if (curFilterIndex == size) {
                isDoPreFilter = false;
                curFilterIndex--;
            }
        } else {
            filterChain.doPostFilter(curFilterIndex--, this);
            if (curFilterIndex < 0) {
                ContextHelper.writeBackResponse(this);
            }
        }
    }
}
