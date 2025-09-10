package cn.cbcstack.gateway.core.filter.loadbalance.strategy;

import cn.cbcstack.gateway.config.pojo.ServiceInstance;
import cn.cbcstack.gateway.core.context.GatewayContext;

import java.util.List;

import static cn.cbcstack.gateway.common.constant.LoadBalanceConstant.CLIENT_IP_LOAD_BALANCE_STRATEGY;

/**
 * 客户端IP负载均衡策略
 */
public class ClientIpLoadBalanceStrategy implements LoadBalanceStrategy {

    @Override
    public ServiceInstance selectInstance(GatewayContext context, List<ServiceInstance> instances) {
        return instances.get(Math.abs(context.getRequest().getHost().hashCode()) % instances.size());
    }

    @Override
    public String mark() {
        return CLIENT_IP_LOAD_BALANCE_STRATEGY;
    }

}
