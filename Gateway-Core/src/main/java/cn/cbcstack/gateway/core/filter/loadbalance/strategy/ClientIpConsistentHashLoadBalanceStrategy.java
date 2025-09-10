package cn.cbcstack.gateway.core.filter.loadbalance.strategy;

import cn.cbcstack.gateway.config.pojo.RouteDefinition;
import cn.cbcstack.gateway.config.pojo.ServiceInstance;
import cn.cbcstack.gateway.core.algorithm.ConsistentHashing;
import cn.cbcstack.gateway.core.context.GatewayContext;
import cn.cbcstack.gateway.core.helper.FilterHelper;

import java.util.List;

import static cn.cbcstack.gateway.common.constant.FilterConstant.LOAD_BALANCE_FILTER_NAME;
import static cn.cbcstack.gateway.common.constant.LoadBalanceConstant.CLIENT_IP_CONSISTENT_HASH_LOAD_BALANCE_STRATEGY;

/**
 * 一致性哈希负载均衡
 */
public class ClientIpConsistentHashLoadBalanceStrategy implements LoadBalanceStrategy {

    @Override
    public ServiceInstance selectInstance(GatewayContext context, List<ServiceInstance> instances) {
        RouteDefinition.LoadBalanceFilterConfig loadBalanceFilterConfig = FilterHelper.findFilterConfigByClass(
                context.getRoute().getFilterConfigs(),
                LOAD_BALANCE_FILTER_NAME,
                RouteDefinition.LoadBalanceFilterConfig.class);
        int virtualNodeNum = 1;
        if (loadBalanceFilterConfig != null && loadBalanceFilterConfig.getVirtualNodeNum() > 0) {
            virtualNodeNum = loadBalanceFilterConfig.getVirtualNodeNum();
        }

        List<String> nodes = instances.stream().map(ServiceInstance::getInstanceId).toList();
        ConsistentHashing consistentHashing = new ConsistentHashing(nodes, virtualNodeNum);
        String selectedNode = consistentHashing.getNode(String.valueOf(context.getRequest().getHost().hashCode()));

        for (ServiceInstance instance : instances) {
            if (instance.getInstanceId().equals(selectedNode)) {
                return instance;
            }
        }

        return instances.get(0);
    }

    @Override
    public String mark() {
        return CLIENT_IP_CONSISTENT_HASH_LOAD_BALANCE_STRATEGY;
    }

}
