package cn.cbcstack.gateway.core.filter.gray.strategy;

import cn.cbcstack.gateway.config.pojo.ServiceInstance;
import cn.cbcstack.gateway.core.context.GatewayContext;

import java.util.List;

public interface GrayStrategy {

    /**
     * 判断是否应该被路由到灰度实例
     */
    boolean shouldRoute2Gray(GatewayContext context, List<ServiceInstance> instances);

    /**
     * 策略名称
     */
    String mark();

}
