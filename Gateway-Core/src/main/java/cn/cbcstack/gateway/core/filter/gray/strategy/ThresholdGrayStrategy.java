package cn.cbcstack.gateway.core.filter.gray.strategy;

import cn.cbcstack.gateway.config.pojo.RouteDefinition;
import cn.cbcstack.gateway.config.pojo.ServiceInstance;
import cn.cbcstack.gateway.core.context.GatewayContext;
import cn.cbcstack.gateway.core.helper.FilterHelper;

import java.util.List;

import static cn.cbcstack.gateway.common.constant.FilterConstant.GRAY_FILTER_NAME;
import static cn.cbcstack.gateway.common.constant.GrayConstant.MAX_GRAY_THRESHOLD;
import static cn.cbcstack.gateway.common.constant.GrayConstant.THRESHOLD_GRAY_STRATEGY;

/**
 * 根据流量决定是否灰度策略, 存在灰度流量机器就走灰度
 */
public class ThresholdGrayStrategy implements GrayStrategy {

    @Override
    public boolean shouldRoute2Gray(GatewayContext context, List<ServiceInstance> instances) {
        if (instances.stream().anyMatch(instance -> instance.isEnabled() && !instance.isGray())) {
            RouteDefinition.GrayFilterConfig grayFilterConfig = FilterHelper.findFilterConfigByClass(context.getRoute().getFilterConfigs(), GRAY_FILTER_NAME, RouteDefinition.GrayFilterConfig.class);
            double maxGrayThreshold = grayFilterConfig == null ? MAX_GRAY_THRESHOLD : grayFilterConfig.getMaxGrayThreshold();
            double grayThreshold = instances.stream().mapToDouble(ServiceInstance::getThreshold).sum();
            grayThreshold = Math.min(grayThreshold, maxGrayThreshold);
            return Math.abs(Math.random() - 1) <= grayThreshold;
        }
        return true;
    }

    @Override
    public String mark() {
        return THRESHOLD_GRAY_STRATEGY;
    }

}
