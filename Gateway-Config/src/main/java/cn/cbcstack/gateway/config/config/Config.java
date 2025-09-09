package cn.cbcstack.gateway.config.config;

import cn.cbcstack.gateway.config.pojo.RouteDefinition;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static cn.cbcstack.gateway.common.constant.ConfigConstant.*;

/**
 * 网关静态配置
 */
@Data
public class Config {

    // base
    private String name = DEFAULT_NAME; // 服务名称
    private int port = DEFAULT_PORT; // 端口
    private String env = DEFAULT_ENV; // 环境

    // 配置中心
    private ConfigCenter configCenter = new ConfigCenter();

    // 路由配置
    private List<RouteDefinition> routes = new ArrayList<>();

    // 超时时间，单位ms
    private int timeout = 3000;

    // 重试次数
    private int retryTimes = 3;

    // 路由需要走的过滤器
    private Set<FilterConfig> filterConfigs;


    @Data
    public static class FilterConfig {

        /**
         * 过滤器名字，唯一的
         */
        private String name;

        /**
         * 是否启用过滤器
         */
        private boolean enable = true;

        /**
         * 过滤器规则描述，json
         */
        private String config;

    }

}

