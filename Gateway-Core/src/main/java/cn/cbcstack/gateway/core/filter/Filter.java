package cn.cbcstack.gateway.core.filter;


import cn.cbcstack.gateway.core.context.GatewayContext;

public interface Filter {

    void doPreFilter(GatewayContext context);

    void doPostFilter(GatewayContext context);

    String mark(); // 标识唯一的过滤器

    int getOrder();

}
