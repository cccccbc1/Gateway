package cn.cbcstack.gateway.config.config;


import lombok.Data;

/**
 * Netty配置项
 */
@Data
public class NettyConfig {

    private int eventLoopGroupBossNum = 1;

    private int eventLoopGroupWorkerNum = Runtime.getRuntime().availableProcessors() * 2;

    private int maxContentLength = 64 * 1024 * 1024; // 64MB

}
