package cn.cbcstack.gateway.core.filter.loadbalance.strategy;

import cn.cbcstack.gateway.config.pojo.ServiceInstance;
import cn.cbcstack.gateway.core.context.GatewayContext;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static cn.cbcstack.gateway.common.constant.LoadBalanceConstant.RANDOM_LOAD_BALANCE_STRATEGY;

/**
 * 随机选取
 */
public class RandomLoadBalanceStrategy implements LoadBalanceStrategy {

    @Override
    public ServiceInstance selectInstance(GatewayContext context, List<ServiceInstance> instances) {
        return instances.get(ThreadLocalRandom.current().nextInt(instances.size()));
    }

    @Override
    public String mark() {
        return RANDOM_LOAD_BALANCE_STRATEGY;
    }

}
