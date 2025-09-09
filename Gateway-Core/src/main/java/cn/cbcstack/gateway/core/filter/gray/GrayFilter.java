package cn.cbcstack.gateway.core.filter.gray;

import cn.cbcstack.gateway.config.manager.DynamicConfigManager;
import cn.cbcstack.gateway.config.pojo.RouteDefinition;
import cn.cbcstack.gateway.config.pojo.ServiceInstance;
import cn.cbcstack.gateway.core.context.GatewayContext;
import cn.cbcstack.gateway.core.filter.Filter;
import cn.cbcstack.gateway.core.filter.gray.strategy.GrayStrategy;
import cn.cbcstack.gateway.core.helper.FilterHelper;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static cn.cbcstack.gateway.common.constant.FilterConstant.GRAY_FILTER_NAME;
import static cn.cbcstack.gateway.common.constant.FilterConstant.GRAY_FILTER_ORDER;

@Slf4j
public class GrayFilter implements Filter {

    // 这里只标记请求是不是走灰度，实际在负载均衡里选择路由机器
    @Override
    public void doPreFilter(GatewayContext context) {
        RouteDefinition.FilterConfig filterConfig = FilterHelper.findFilterConfigByName(context.getRoute().getFilterConfigs(), GRAY_FILTER_NAME);
        if (filterConfig == null) {
            filterConfig = FilterHelper.buildDefaultGrayFilterConfig();
        }
        if (!filterConfig.isEnable()) {
            return;
        }

        // 获取服务所有实例
        List<ServiceInstance> instances = DynamicConfigManager.getInstance()
                .getInstancesByServiceName(context.getRequest().getServiceDefinition().getServiceName())
                .values().stream().toList();

        if (instances.stream().anyMatch(instance -> instance.isEnabled() && instance.isGray())) {
            // 存在灰度实例
            GrayStrategy strategy = selectGrayStrategy(JSONUtil.toBean(filterConfig.getConfig(), RouteDefinition.GrayFilterConfig.class));
            context.getRequest().setGray(strategy.shouldRoute2Gray(context, instances));
        } else {
            // 没有灰度实例，不走灰度
            context.getRequest().setGray(false);
        }
        context.doFilter();
    }

    @Override
    public void doPostFilter(GatewayContext context) {
        context.doFilter();
    }

    @Override
    public String mark() {
        return GRAY_FILTER_NAME;
    }

    @Override
    public int getOrder() {
        return GRAY_FILTER_ORDER;
    }

    private GrayStrategy selectGrayStrategy(RouteDefinition.GrayFilterConfig grayFilterConfig) {
        return GrayStrategyManager.getStrategy(grayFilterConfig.getStrategyName());
    }

}
