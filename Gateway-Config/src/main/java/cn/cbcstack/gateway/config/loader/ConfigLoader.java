package cn.cbcstack.gateway.config.loader;

import cn.cbcstack.gateway.config.config.GatewayConfig;
import cn.cbcstack.gateway.config.util.ConfigUtil;
import lombok.val;

import static cn.cbcstack.gateway.common.constant.ConfigConstant.CONFIG_PATH;
import static cn.cbcstack.gateway.common.constant.ConfigConstant.CONFIG_PREFIX;

public class ConfigLoader {

    public static GatewayConfig load(String[] args) {
        GatewayConfig gatewayConfig = ConfigUtil.loadConfigFromYaml(CONFIG_PATH, GatewayConfig.class, CONFIG_PREFIX);
        return gatewayConfig != null ? gatewayConfig: new GatewayConfig();
    }

}
