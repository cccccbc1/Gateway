package cn.cbcstack.gateway.config.loader;

import cn.cbcstack.gateway.config.config.GatewayConfig;
import cn.cbcstack.gateway.config.util.ConfigUtil;

import static cn.cbcstack.gateway.common.constant.ConfigConstant.CONFIG_PATH;
import static cn.cbcstack.gateway.common.constant.ConfigConstant.CONFIG_PREFIX;

public class ConfigLoader {

    public static GatewayConfig load(String[] args) {
        // todo 如果nacos上配置yaml里不配置应该也能正常启动，而不是空指针
        return ConfigUtil.loadConfigFromYaml(CONFIG_PATH, GatewayConfig.class, CONFIG_PREFIX);
    }

}
