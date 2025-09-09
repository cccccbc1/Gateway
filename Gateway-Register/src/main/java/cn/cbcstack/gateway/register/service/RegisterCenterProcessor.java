package cn.cbcstack.gateway.register.service;

import cn.cbcstack.gateway.config.config.*;

/**
 * 注册中心接口
 */
public interface RegisterCenterProcessor {

    /**
     * 注册中心初始化
     */
    void init(GatewayConfig config);

    /**
     * 订阅注册中心实例变化
     */
    void subscribeServiceChange(RegisterCenterListener listener);

}
