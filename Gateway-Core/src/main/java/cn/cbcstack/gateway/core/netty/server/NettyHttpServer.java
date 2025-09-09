package cn.cbcstack.gateway.core.netty.server;

import cn.cbcstack.gateway.common.util.SystemUtil;
import cn.cbcstack.gateway.config.config.GatewayConfig;
import cn.cbcstack.gateway.config.config.NettyConfig;
import cn.cbcstack.gateway.core.netty.LifeCycle;
import cn.cbcstack.gateway.core.netty.handler.NettyHttpServerHandler;
import cn.cbcstack.gateway.core.netty.processor.NettyProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.SneakyThrows;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Netty 服务端
 */
public class NettyHttpServer implements LifeCycle {

    private final GatewayConfig config;
    private final NettyProcessor nettyProcessor;
    private final AtomicBoolean start = new AtomicBoolean(false);
    private ServerBootstrap serverBootstrap;
    private EventLoopGroup eventLoopGroupBoss;
    private EventLoopGroup eventLoopGroupWorker;

    public NettyHttpServer(GatewayConfig config, NettyProcessor nettyProcessor) {
        this.config = config;
        this.nettyProcessor = nettyProcessor;
        init();
    }

    private void init() {
        this.serverBootstrap = new ServerBootstrap();
        if (SystemUtil.useEpoll()) {
            this.eventLoopGroupBoss = new EpollEventLoopGroup(config.getNetty().getEventLoopGroupBossNum(),
                    new DefaultThreadFactory("epoll-netty-boss-nio"));
            this.eventLoopGroupWorker = new EpollEventLoopGroup(config.getNetty().getEventLoopGroupWorkerNum(),
                    new DefaultThreadFactory("epoll-netty-worker-nio"));
        } else {
            this.eventLoopGroupBoss = new NioEventLoopGroup(config.getNetty().getEventLoopGroupBossNum(),
                    new DefaultThreadFactory("default-netty-boss-nio"));
            this.eventLoopGroupWorker = new NioEventLoopGroup(config.getNetty().getEventLoopGroupWorkerNum(),
                    new DefaultThreadFactory("default-netty-worker-nio"));
        }
    }

        // 启动Netty服务器
        @SneakyThrows(InterruptedException.class)
        @Override
        public void start() {
            if(!start.compareAndSet(false, true)) {
                return;
            }

            serverBootstrap
                    .group(eventLoopGroupBoss, eventLoopGroupWorker)
                    .channel(SystemUtil.useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)            // TCP连接的最大队列长度
                    .option(ChannelOption.SO_REUSEADDR, true)          // 允许端口重用
                    .option(ChannelOption.SO_KEEPALIVE, true)          // 保持连接检测
                    .childOption(ChannelOption.TCP_NODELAY, true)      // 禁用Nagle算法，适用于小数据即时传输
                    .childOption(ChannelOption.SO_SNDBUF, 65535)       // 设置发送缓冲区大小, 64KB
                    .childOption(ChannelOption.SO_RCVBUF, 65535)       // 设置接收缓冲区大小
                    .localAddress(new InetSocketAddress(config.getPort()))   // 绑定监听端口
                    .childHandler(new ChannelInitializer<>() {   // 定义处理新连接的管道初始化逻辑
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new HttpServerCodec(), // 处理HTTP请求的编解码器
                                    new HttpObjectAggregator(config.getNetty().getMaxContentLength()), // 聚合HTTP请求
                                    new HttpServerExpectContinueHandler(), // 处理HTTP 100 Continue请求
                                    new NettyHttpServerHandler(nettyProcessor) // 自定义的处理器
                            );
                        }
                    });

        }

    @Override
    public void shutdown() {

    }

}
