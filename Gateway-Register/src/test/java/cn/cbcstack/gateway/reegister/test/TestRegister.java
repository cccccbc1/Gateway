package cn.cbcstack.gateway.reegister.test;

import cn.cbcstack.gateway.config.config.GatewayConfig;
import cn.cbcstack.gateway.config.loader.ConfigLoader;
import cn.cbcstack.gateway.register.service.impl.nacos.NacosRegisterCenter;
import org.junit.Test;

public class TestRegister {

    @Test
    public void testRegister() {
        GatewayConfig config = ConfigLoader.load(null);
        NacosRegisterCenter center = new NacosRegisterCenter();
        center.init(config);
        while(true) {}
    }

    @Test
    public void testRegisterSub() {
        GatewayConfig config = ConfigLoader.load(null);
        NacosRegisterCenter center = new NacosRegisterCenter();
        center.init(config);
        center.subscribeServiceChange(((serviceDefinition, newInstances) -> {
            System.out.println(serviceDefinition);
            System.out.println(newInstances);
        }));
        while(true) {}
    }

}
