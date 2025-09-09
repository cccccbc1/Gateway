package cn.cbcstack.gateway.core.context;

import cn.cbcstack.gateway.config.pojo.RouteDefinition;
import cn.cbcstack.gateway.core.dto.request.GatewayRequest;
import cn.cbcstack.gateway.core.dto.response.GatewayResponse;
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

}
