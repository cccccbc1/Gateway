package cn.cbcstack.gateway.core.netty;

public interface LifeCycle {

    /**
     * 启动
     */
    void start();

    /**
     * 关闭
     */
    void shutdown();

}
