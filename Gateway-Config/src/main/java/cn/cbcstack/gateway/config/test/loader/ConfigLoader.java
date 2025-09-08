package cn.cbcstack.gateway.config.test.loader;

import cn.cbcstack.gateway.config.test.config.Config;
import cn.cbcstack.gateway.config.test.util.ConfigUtil;

import static cn.cbcstack.gateway.common.constant.ConfigConstant.CONFIG_PATH;
import static cn.cbcstack.gateway.common.constant.ConfigConstant.CONFIG_PREFIX;

public class ConfigLoader {

    public static Config load(String[] args) {
        return ConfigUtil.loadConfigFromYaml(CONFIG_PATH, Config.class, CONFIG_PREFIX);
    }

}
