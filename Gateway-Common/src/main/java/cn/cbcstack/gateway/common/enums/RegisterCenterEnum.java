package cn.cbcstack.gateway.common.enums;

import lombok.Getter;

@Getter
public enum RegisterCenterEnum {

    NACOS("nacos"),
    ZOOKEEPER("zookeeper");

    private final String desc;

    RegisterCenterEnum(String desc) {
        this.desc = desc;
    }

}
