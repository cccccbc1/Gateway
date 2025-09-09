package cn.cbcstack.gateway.register.service.impl.nacos;

import cn.cbcstack.gateway.config.config.GatewayConfig;
import cn.cbcstack.gateway.config.config.RegisterCenter;
import cn.cbcstack.gateway.config.pojo.ServiceDefinition;
import cn.cbcstack.gateway.config.pojo.ServiceInstance;
import cn.cbcstack.gateway.register.service.RegisterCenterListener;
import cn.cbcstack.gateway.register.service.RegisterCenterProcessor;
import cn.cbcstack.gateway.register.util.NetUtil;
import cn.hutool.core.bean.BeanUtil;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingMaintainFactory;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.Service;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import com.alibaba.nacos.common.executor.NameThreadFactory;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;



@Slf4j
public class NacosRegisterCenter implements RegisterCenterProcessor {

    /**
     * 注册中心配置
     */
    private GatewayConfig config;

    /**
     * 主要用于维护服务实例信息
     */
    private NamingService namingService;

    /**
     * 主要用于维护服务定义信息
     */
    private NamingMaintainService namingMaintainService;

    /**
     * 监听器
     */
    private RegisterCenterListener listener;

    private final AtomicBoolean init = new AtomicBoolean(false);


    @SneakyThrows(Exception.class)
    @Override
    public void init(GatewayConfig config) {
        if (!init.compareAndSet(false, true)) return;
        this.config = config;

        String group = config.getRegisterCenter().getNacos().getGroup();

        Properties properties = buildProperties(config.getRegisterCenter());
        namingService = NamingFactory.createNamingService(properties);
        namingMaintainService = NamingMaintainFactory.createMaintainService(properties);

        // 将网关自己注册到注册中心
        Instance instance = new Instance();
        instance.setInstanceId(NetUtil.getLocalIp() + ":" + config.getPort());
        instance.setIp(NetUtil.getLocalIp());
        instance.setPort(config.getPort());
        namingService.registerInstance(config.getName(), group, instance);
        log.info("gateway instance register: {}", instance);

        // 设置网关服务元数据信息
        Map<String, String> serviceInfo = BeanUtils.describe(new ServiceDefinition(config.getName()));
        namingMaintainService.updateService(config.getName(), group, 0, serviceInfo);
        log.info("gateway service meta register: {}", serviceInfo);
    }

    /**
     * 每 10 秒轮询发现新服务并订阅
     * @param listener
     */
    @Override
    public void subscribeServiceChange(RegisterCenterListener listener) {
        this.listener = listener;

        Executors.newScheduledThreadPool(1, new NameThreadFactory("doSubscribeAllServices")).
                scheduleWithFixedDelay(this::doSubscribeAllServices, 0, 10, TimeUnit.SECONDS);
    }

    private Properties buildProperties(RegisterCenter registerCenter) {
        ObjectMapper mapper = new ObjectMapper();
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, registerCenter.getAddress());
        Map map = mapper.convertValue(registerCenter.getNacos(), Map.class);
        if (map == null || map.isEmpty()) return properties;
        properties.putAll(map);
        return properties;
    }

    private void doSubscribeAllServices() {
        try {
            String group = config.getRegisterCenter().getNacos().getGroup();

            Set<String> subscribeServiceSet = namingService.getSubscribeServices().stream().map(ServiceInfo::getName).collect(Collectors.toSet());

            int pageNo = 1;
            int pageSize = 100;

            List<String> serviceList = namingService.getServicesOfServer(pageNo, pageSize, group).getData();

            while (CollectionUtils.isNotEmpty(serviceList)) {
                for (String serviceName : serviceList) {
                    if (subscribeServiceSet.contains(serviceName)) {
                        continue;
                    }

                    EventListener eventListener = new NacosRegisterListener();
                    // 订阅服务
                    namingService.subscribe(serviceName, group, eventListener);
                    // 首次订阅新服务，主动发起一次信号，刷新缓存
                    eventListener.onEvent(new NamingEvent(serviceName, null));
                    log.info("subscribe a service, ServiceName: {} Group: {}", serviceName, group);
                }
                //遍历下一页的服务列表
                serviceList = namingService.getServicesOfServer(++pageNo, pageSize, group).getData();
            }
        } catch (Exception e) { // 任务中捕捉Exception，防止线程池停止
            log.error("subscribe services from nacos occur exception: {}", e.getMessage(), e);
        }
    }

    private class NacosRegisterListener implements EventListener {

        /**
         * 已订阅服务发生变化时触发，刷新缓存
         * @param event 变化事件
         */
        @SneakyThrows(NacosException.class)
        @Override
        public void onEvent(Event event) {
            if (event instanceof NamingEvent namingEvent) {
                String serviceName = namingEvent.getServiceName();
                String group = config.getRegisterCenter().getNacos().getGroup();

                Service service = namingMaintainService.queryService(serviceName, group);
                ServiceDefinition serviceDefinition = new ServiceDefinition(service.getName());
                BeanUtil.fillBeanWithMap(service.getMetadata(), serviceDefinition, true);

                //获取所有服务实例信息
                List<Instance> allInstances = namingService.getAllInstances(serviceName, group);
                Set<ServiceInstance> newInstances = new HashSet<>();

                if (CollectionUtils.isNotEmpty(allInstances)) {
                    for (Instance instance : allInstances) {
                        if (instance == null) continue;

                        ServiceInstance newInstance = new ServiceInstance();
                        BeanUtil.copyProperties(instance, newInstance);
                        // metaData 在 nacos 上配置的参数，这里是个 map
                        BeanUtil.fillBeanWithMap(instance.getMetadata(), newInstance, true);

                        newInstances.add(newInstance);
                    }
                }

                //调用我们自己的订阅监听器
                listener.onInstancesChange(serviceDefinition, newInstances);
            }
        }

    }
}
