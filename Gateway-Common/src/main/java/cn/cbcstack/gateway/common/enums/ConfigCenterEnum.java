package cn.cbcstack.gateway.common.enums;

import lombok.Getter;

@Getter
public enum ConfigCenterEnum {

    NACOS("nacos"),
    ZOOKEEPER("zookeeper");

    private final String desc;

    ConfigCenterEnum(String desc) {
        this.desc = desc;
    }

}
