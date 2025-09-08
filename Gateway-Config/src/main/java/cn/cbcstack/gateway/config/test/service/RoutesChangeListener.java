package cn.cbcstack.gateway.config.test.service;

import cn.cbcstack.gateway.config.test.pojo.RouteDefinition;

import java.util.List;

/**
 * 路由规则变更监听
 */
public interface RoutesChangeListener {

    /**
     * 路由变更时调用此方法
     */
    void onRoutesChange(List<RouteDefinition> newRoutes);

}
