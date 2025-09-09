package cn.cbcstack.gateway.common.constant;


import cn.cbcstack.gateway.common.enums.RegisterCenterEnum;

public class RegisterCenterConstant {

    public static RegisterCenterEnum REGISTER_CENTER_DEFAULT_IMPL = RegisterCenterEnum.NACOS; // 注册中心默认实现

    public static String REGISTER_CENTER_DEFAULT_ADDRESS = "127.0.0.1:8848"; // 默认注册中心地址

}
