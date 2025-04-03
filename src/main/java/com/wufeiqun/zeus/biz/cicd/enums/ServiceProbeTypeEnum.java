package com.wufeiqun.zeus.biz.cicd.enums;

import lombok.Getter;


/**
 * 应用探活方式枚举
 */
@Getter
public enum ServiceProbeTypeEnum {
    HTTP("HTTP","提供一个GET请求接口, 正常返回200即可"),
    TCP("TCP","TCP端口探活"),
    NONE("NONE","无需探活, 一般用于jar包推私服/前端等");

    private final String type;
    private final String desc;

    ServiceProbeTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
