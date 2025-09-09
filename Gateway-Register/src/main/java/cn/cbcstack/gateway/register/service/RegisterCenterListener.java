package cn.cbcstack.gateway.register.service;


import cn.cbcstack.gateway.config.pojo.ServiceDefinition;
import cn.cbcstack.gateway.config.pojo.ServiceInstance;

import java.util.Set;

/**
 * 注册实例变化监听器
 */
public interface RegisterCenterListener {

    /**
     * 某服务有实例变化时调用此方法
     */
    void onInstancesChange(ServiceDefinition serviceDefinition, Set<ServiceInstance> newInstances);

}
