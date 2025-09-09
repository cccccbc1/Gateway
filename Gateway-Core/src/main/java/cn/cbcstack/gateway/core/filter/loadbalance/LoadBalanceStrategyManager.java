package cn.cbcstack.gateway.core.filter.loadbalance;

import cn.cbcstack.gateway.core.filter.loadbalance.strategy.LoadBalanceStrategy;
import cn.cbcstack.gateway.core.filter.loadbalance.strategy.RoundRobinLoadBalanceStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import static cn.cbcstack.gateway.common.constant.LoadBalanceConstant.ROUND_ROBIN_LOAD_BALANCE_STRATEGY;


@Slf4j
public class LoadBalanceStrategyManager {

    private static final Map<String, LoadBalanceStrategy> strategyMap = new HashMap<>();

    static {
        ServiceLoader<LoadBalanceStrategy> serviceLoader = ServiceLoader.load(LoadBalanceStrategy.class);
        for (LoadBalanceStrategy strategy : serviceLoader) {
            strategyMap.put(strategy.mark(), strategy);
            log.info("load loadbalance strategy success: {}", strategy);
        }
    }

    public static LoadBalanceStrategy getStrategy(String name) {
        LoadBalanceStrategy strategy = strategyMap.get(name);
        if (strategy == null) {
            strategy = new RoundRobinLoadBalanceStrategy();
            strategyMap.put(strategy.mark(), strategy);
        }
        return strategy;
    }

}
