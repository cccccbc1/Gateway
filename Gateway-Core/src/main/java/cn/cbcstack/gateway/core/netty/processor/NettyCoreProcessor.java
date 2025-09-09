package cn.cbcstack.gateway.core.netty.processor;

import cn.cbcstack.gateway.common.enums.ResponseCode;
import cn.cbcstack.gateway.common.exception.GatewayException;
import cn.cbcstack.gateway.core.context.GatewayContext;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyCoreProcessor implements NettyProcessor{
    @Override
    public void process(ChannelHandlerContext ctx, FullHttpRequest request) {
        // todo 核心处理逻辑
    }

    private void doWriteAndRelease(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse httpResponse) {
        ctx.writeAndFlush(httpResponse)
                .addListener(ChannelFutureListener.CLOSE); // 发送响应后关闭通道
        ReferenceCountUtil.release(request); // 释放与请求相关联的资源
    }
}
