package cn.cbcstack.gateway.core.filter.loadbalance.strategy;


import cn.cbcstack.gateway.config.pojo.ServiceInstance;
import cn.cbcstack.gateway.core.context.GatewayContext;

import java.util.List;

import static cn.cbcstack.gateway.common.constant.LoadBalanceConstant.GRAY_LOAD_BALANCE_STRATEGY;

/**
 * 灰度负载均衡, 从灰度机器中随机选一台路由
 */
public class GrayLoadBalanceStrategy implements LoadBalanceStrategy {

    @Override
    public ServiceInstance selectInstance(GatewayContext context, List<ServiceInstance> instances) {
        int totalThreshold = (int) (instances.stream().mapToDouble(ServiceInstance::getThreshold).sum() * 100);
        if (totalThreshold <= 0) return null;
        double randomThreshold = Math.abs(context.getRequest().getHost().hashCode()) % totalThreshold;
        for (ServiceInstance instance : instances) {
            randomThreshold -= instance.getThreshold();
            if (randomThreshold < 0) return instance;
        }
        return null;
    }

    @Override
    public String mark() {
        return GRAY_LOAD_BALANCE_STRATEGY;
    }

}
