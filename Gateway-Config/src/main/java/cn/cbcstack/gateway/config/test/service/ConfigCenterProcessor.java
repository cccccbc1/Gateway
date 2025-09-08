package cn.cbcstack.gateway.config.test.service;

import cn.cbcstack.gateway.config.test.config.ConfigCenter;

/**
 * 配置中心
 */
public interface ConfigCenterProcessor {

    /**
     * 初始化配置中心配置
     */
    void init(ConfigCenter configCenter);

    /**
     * 订阅配置中心配置变更
     */
    void subscribeRoutesChange(RoutesChangeListener listener);

}
