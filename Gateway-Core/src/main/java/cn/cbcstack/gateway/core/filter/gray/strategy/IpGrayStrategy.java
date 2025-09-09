package cn.cbcstack.gateway.core.filter.gray.strategy;

import cn.cbcstack.gateway.config.pojo.RouteDefinition;
import cn.cbcstack.gateway.config.pojo.ServiceInstance;
import cn.cbcstack.gateway.core.context.GatewayContext;
import cn.cbcstack.gateway.core.helper.FilterHelper;

import java.util.List;

import static cn.cbcstack.gateway.common.constant.FilterConstant.GRAY_FILTER_NAME;
import static cn.cbcstack.gateway.common.constant.GrayConstant.CLIENT_IP_GRAY_STRATEGY;

/**
 * 根据 IP 灰度
 */
public class IpGrayStrategy implements GrayStrategy {
    @Override
    public boolean shouldRoute2Gray(GatewayContext context, List<ServiceInstance> instances) {
        // 如果没有可用的非灰度实例，则都走灰度
        if (instances.stream().anyMatch(instance -> instance.isEnabled() && !instance.isGray())) {
            // 计算所有灰度实例能承担的最大流量，根据客户端ip hash判断走不走灰度请求
            RouteDefinition.GrayFilterConfig grayFilterConfig = FilterHelper.findFilterConfigByClass(
                    context.getRoute().getFilterConfigs(),
                    GRAY_FILTER_NAME,
                    RouteDefinition.GrayFilterConfig.class);
            double grayThreshold = instances.stream().mapToDouble(ServiceInstance::getThreshold).sum();
            grayThreshold = Math.min(grayThreshold, grayFilterConfig.getMaxGrayThreshold());
            return Math.abs(context.getRequest().getHost().hashCode()) % 100 <= grayThreshold * 100;
        }
        return true;
    }

    @Override
    public String mark() {
        return CLIENT_IP_GRAY_STRATEGY;
    }
}
