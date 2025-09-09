package cn.cbcstack.gateway.core.filter;

import cn.cbcstack.gateway.config.manager.DynamicConfigManager;
import cn.cbcstack.gateway.config.pojo.RouteDefinition;
import cn.cbcstack.gateway.core.context.GatewayContext;
import cn.hutool.core.collection.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static cn.cbcstack.gateway.common.constant.FilterConstant.*;

@Slf4j
public class FilterChainFactory {

    private static final Map<String, Filter> filterMap = new HashMap<>();

    private static final Map<String, FilterChain> filterChainMap = new ConcurrentHashMap<>();

    private static final Set<String> addListener = new ConcurrentHashSet<>();

    static {
        ServiceLoader<Filter> serviceLoader = ServiceLoader.load(Filter.class);
        for (Filter filter : serviceLoader) {
            filterMap.put(filter.mark(), filter);
            log.info("load filter success: {}", filter);
        }
    }

    public static void buildFilterChain(GatewayContext ctx) {
        String serviceName = ctx.getRoute().getServiceName();
        FilterChain filterChain = filterChainMap.computeIfAbsent(serviceName, name -> {
            FilterChain chain = new FilterChain();
            // 前置过滤器：包括跨域、限流、灰度、负载均衡
            addPreFilter(chain);
            // 用户自定义过滤器
            addFilter(chain, ctx.getRoute().getFilterConfigs());
            // 后置过滤器
            addPostFilter(chain);
            chain.sort();
            if (!addListener.contains(serviceName)) {
                // 注册事件到监听器
                DynamicConfigManager.getInstance().addRouteListener(serviceName, newRoute ->
                        filterChainMap.remove(newRoute.getServiceName()));
                addListener.add(serviceName);
            }
            return chain;
        });
        ctx.setFilterChain(filterChain);
    }

    private static void addPreFilter(FilterChain chain) {
        addFilterIfPresent(chain, CORS_FILTER_NAME);
        addFilterIfPresent(chain, FLOW_FILTER_NAME);
        addFilterIfPresent(chain, GRAY_FILTER_NAME);
        addFilterIfPresent(chain, LOAD_BALANCE_FILTER_NAME);
    }

    private static void addFilter(FilterChain chain, Set<RouteDefinition.FilterConfig> filterConfigs) {
        if (filterConfigs == null || filterConfigs.isEmpty()) return;
        for (RouteDefinition.FilterConfig filterConfig : filterConfigs) {
            if (!addFilterIfPresent(chain, filterConfig.getName())) {
                log.info("not found filter: {}", filterConfig.getName());
            }
        }
    }

    private static void addPostFilter(FilterChain chain) {
        addFilterIfPresent(chain, ROUTE_FILTER_NAME);
    }

    private static boolean addFilterIfPresent(FilterChain chain, String filterName) {
        Filter filter = filterMap.get(filterName);
        if (null != filter) {
            chain.add(filter);
            return true;
        }
        return false;
    }


}
