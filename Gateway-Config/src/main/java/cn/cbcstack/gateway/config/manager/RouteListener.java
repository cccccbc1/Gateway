package cn.cbcstack.gateway.config.manager;

import cn.cbcstack.gateway.config.pojo.RouteDefinition;

public interface RouteListener {

    void changeOnRoute(RouteDefinition routeDefinition);

}
