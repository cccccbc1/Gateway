package cn.cbcstack.gateway.config;

import cn.cbcstack.gateway.config.config.Config;
import cn.cbcstack.gateway.config.loader.ConfigLoader;
import org.junit.Before;
import org.junit.Test;

public class LoadTest {

    Config config;

    @Before
    public void before() {
        this.config = ConfigLoader.load(null);
    }

    @Test
    public void testConfigLoad() {
        System.out.println(config);
    }
}
