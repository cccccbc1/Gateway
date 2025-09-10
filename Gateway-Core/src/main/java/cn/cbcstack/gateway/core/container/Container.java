package cn.cbcstack.gateway.core.container;

import cn.cbcstack.gateway.config.config.GatewayConfig;
import cn.cbcstack.gateway.core.netty.client.NettyHttpClient;
import cn.cbcstack.gateway.core.netty.processor.NettyCoreProcessor;
import cn.cbcstack.gateway.core.netty.server.NettyHttpServer;

import java.util.concurrent.atomic.AtomicBoolean;

public class Container implements LifeCycle {

    private final NettyHttpServer nettyHttpServer;

    private final NettyHttpClient nettyHttpClient;

    private final AtomicBoolean start = new AtomicBoolean(false);


    public Container(GatewayConfig config) {
        this.nettyHttpServer = new NettyHttpServer(config, new NettyCoreProcessor());
        this.nettyHttpClient = new NettyHttpClient(config);
    }

    @Override
    public void start() {
        if (!start.compareAndSet(false, true)) return;
        nettyHttpServer.start();
        nettyHttpClient.start();
    }

    @Override
    public void shutdown() {
        if (!start.get()) return;
        nettyHttpServer.shutdown();
        nettyHttpClient.shutdown();
    }

    @Override
    public boolean isStarted() {
        return start.get();
    }

}
