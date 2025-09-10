package cn.cbcstack.gateway.config;

import cn.cbcstack.gateway.config.config.GatewayConfig;
import cn.cbcstack.gateway.config.loader.ConfigLoader;
import cn.cbcstack.gateway.config.service.ConfigCenterProcessor;
import cn.cbcstack.gateway.config.service.impl.nacos.NacosConfigCenterProcessor;
import org.junit.Before;
import org.junit.Test;

public class LoadTest {

    GatewayConfig config;

    @Before
    public void before() {
        this.config = ConfigLoader.load(null);
    }

    @Test
    public void testConfigLoad() {
        System.out.println(config);
    }

    @Test
    public void testNacosConfig() {
        ConfigCenterProcessor processor = new NacosConfigCenterProcessor();
        processor.init(config.getConfigCenter());
        processor.subscribeRoutesChange(i -> {});
    }
}
