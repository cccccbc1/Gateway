package cn.cbcstack.gateway.core.filter.loadbalance.strategy;

import cn.cbcstack.gateway.config.pojo.RouteDefinition;
import cn.cbcstack.gateway.config.pojo.ServiceInstance;
import cn.cbcstack.gateway.core.context.GatewayContext;
import cn.cbcstack.gateway.core.helper.FilterHelper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.cbcstack.gateway.common.constant.FilterConstant.LOAD_BALANCE_FILTER_NAME;
import static cn.cbcstack.gateway.common.constant.LoadBalanceConstant.ROUND_ROBIN_LOAD_BALANCE_STRATEGY;

/**
 * 轮循策略
 */
public class RoundRobinLoadBalanceStrategy implements LoadBalanceStrategy {

    private final Map<String, AtomicInteger> strictPositionMap = new ConcurrentHashMap<>();

    Map<String, Integer> positionMap = new ConcurrentHashMap<>();

    // 防止超过最大值
    private final int THRESHOLD = Integer.MAX_VALUE >> 2;

    @Override
    public ServiceInstance selectInstance(GatewayContext context, List<ServiceInstance> instances) {
        boolean isStrictRoundRobin = true;
        RouteDefinition.LoadBalanceFilterConfig loadBalanceFilterConfig = FilterHelper.findFilterConfigByClass(
                context.getRoute().getFilterConfigs(),
                LOAD_BALANCE_FILTER_NAME,
                RouteDefinition.LoadBalanceFilterConfig.class);
        if (loadBalanceFilterConfig != null) {
            isStrictRoundRobin = loadBalanceFilterConfig.isStrictRoundRobin();
        }
        String serviceName = context.getRequest().getServiceDefinition().getServiceName();
        ServiceInstance serviceInstance;
        // 严格轮询, 用 AtomicInteger
        if (isStrictRoundRobin) {
            AtomicInteger strictPosition = strictPositionMap.computeIfAbsent(serviceName, k -> new AtomicInteger(0));
            int index = Math.abs(strictPosition.getAndIncrement());
            serviceInstance = instances.get(index % instances.size());
            if (index >= THRESHOLD) {
                strictPosition.set((index + 1) % instances.size());
            }
        } else {
            // 非严格, 用 int
            int position = positionMap.getOrDefault(serviceName, 0);
            int index = Math.abs(position++);
            serviceInstance = instances.get(index % instances.size());
            if (position >= THRESHOLD) {
                positionMap.put(serviceName, (position + 1) % instances.size());
            }
        }
        return serviceInstance;
    }

    @Override
    public String mark() {
        return ROUND_ROBIN_LOAD_BALANCE_STRATEGY;
    }

}
