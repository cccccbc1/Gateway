package cn.cbcstack.gateway.core.filter.loadbalance;

import cn.cbcstack.gateway.common.enums.ResponseCode;
import cn.cbcstack.gateway.common.exception.NotFoundException;
import cn.cbcstack.gateway.config.manager.DynamicConfigManager;
import cn.cbcstack.gateway.config.pojo.RouteDefinition;
import cn.cbcstack.gateway.config.pojo.ServiceInstance;
import cn.cbcstack.gateway.core.context.GatewayContext;
import cn.cbcstack.gateway.core.filter.Filter;
import cn.cbcstack.gateway.core.filter.loadbalance.strategy.GrayLoadBalanceStrategy;
import cn.cbcstack.gateway.core.filter.loadbalance.strategy.LoadBalanceStrategy;
import cn.cbcstack.gateway.core.helper.FilterHelper;
import cn.hutool.json.JSONUtil;

import java.util.List;

import static cn.cbcstack.gateway.common.constant.FilterConstant.LOAD_BALANCE_FILTER_NAME;
import static cn.cbcstack.gateway.common.constant.FilterConstant.LOAD_BALANCE_FILTER_ORDER;

public class LoadBalanceFilter implements Filter {


    @Override
    public void doPreFilter(GatewayContext context) {
        RouteDefinition.FilterConfig filterConfig = FilterHelper.findFilterConfigByName(context.getRoute().getFilterConfigs(), LOAD_BALANCE_FILTER_NAME);
        if (filterConfig == null) {
            filterConfig = FilterHelper.buildDefaultLoadBalanceFilterConfig();
        }
        // 获取服务所有实例
        List<ServiceInstance> instances = DynamicConfigManager.getInstance()
                .getInstancesByServiceName(context.getRequest().getServiceDefinition().getServiceName())
                .values().stream().toList();

        LoadBalanceStrategy strategy;
        if (context.getRequest().isGray()) {
            strategy = new GrayLoadBalanceStrategy(); // 灰度负载均衡策略
            // 如果请求是灰度的，再进行一遍过滤
            instances = instances.stream().filter(instance -> instance.isEnabled() && instance.isGray()).toList();
        } else {
            strategy = selectLoadBalanceStrategy(JSONUtil.toBean(filterConfig.getConfig(), RouteDefinition.LoadBalanceFilterConfig.class));
        }
        if (instances.isEmpty()) {
            throw new NotFoundException(ResponseCode.SERVICE_INSTANCE_NOT_FOUND);
        }
        ServiceInstance serviceInstance = strategy.selectInstance(context, instances);
        if (null == serviceInstance) {
            throw new NotFoundException(ResponseCode.SERVICE_INSTANCE_NOT_FOUND);
        }
        context.getRequest().setModifyHost(serviceInstance.getIp() + ":" + serviceInstance.getPort());
        context.doFilter();
    }

    @Override
    public void doPostFilter(GatewayContext context) {
        context.doFilter();
    }

    @Override
    public String mark() {
        return LOAD_BALANCE_FILTER_NAME;
    }

    @Override
    public int getOrder() {
        return LOAD_BALANCE_FILTER_ORDER;
    }

    private LoadBalanceStrategy selectLoadBalanceStrategy(RouteDefinition.LoadBalanceFilterConfig loadBalanceFilterConfig) {
        return LoadBalanceStrategyManager.getStrategy(loadBalanceFilterConfig.getStrategyName());
    }
}
