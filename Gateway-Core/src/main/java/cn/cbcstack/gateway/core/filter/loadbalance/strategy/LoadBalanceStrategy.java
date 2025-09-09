package cn.cbcstack.gateway.core.filter.loadbalance.strategy;

import cn.cbcstack.gateway.config.pojo.ServiceInstance;
import cn.cbcstack.gateway.core.context.GatewayContext;

import java.util.List;

public interface LoadBalanceStrategy {

    ServiceInstance selectInstance(GatewayContext context, List<ServiceInstance> instances);

    String mark();

}
