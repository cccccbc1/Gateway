package cn.cbcstack.gateway.config.config;

import cn.cbcstack.gateway.common.constant.RegisterCenterConstant;
import cn.cbcstack.gateway.common.enums.RegisterCenterEnum;
import lombok.Data;

/**
 * 注册中心配置
 */
@Data
public class RegisterCenter {

    private RegisterCenterEnum type = RegisterCenterConstant.REGISTER_CENTER_DEFAULT_IMPL; // 注册中心实现

    private String address = RegisterCenterConstant.REGISTER_CENTER_DEFAULT_ADDRESS; // 注册中心地址

    private NacosConfig nacos = new NacosConfig(); // 注册中心nacos配置

}
