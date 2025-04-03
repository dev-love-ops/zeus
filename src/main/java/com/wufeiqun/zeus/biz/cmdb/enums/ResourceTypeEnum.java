package com.wufeiqun.zeus.biz.cmdb.enums;

import lombok.Getter;

/**
 * @author wufeiqun
 * @date 2022-07-12
 * 资源类型枚举, 用于应用资源关系等
 */
@Getter
public enum ResourceTypeEnum {
    /**
     * 服务器
     */
    SERVER("SERVER","服务器"),

    /**
     * MySQL
     */
    MYSQL("MYSQL","MySQL数据库"),

    /**
     * Redis
     */
    REDIS("REDIS","Redis缓存数据库");

    private final String type;
    private final String name;


    ResourceTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }
}
