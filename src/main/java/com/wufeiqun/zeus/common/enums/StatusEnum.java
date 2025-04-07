package com.wufeiqun.zeus.common.enums;

import lombok.Getter;

/**
 * @author wufeiqun
 * @date 2022-07-12
 */
@Getter
public enum StatusEnum {
    /**
     * 启用
     */
    ENABLED("启用状态", 1),
    /**
     * 禁用
     */
    DISABLED("禁用状态", 0);

    /**
     * 状态名称
     */
    private final String name;
    /**
     * 状态code
     */
    private final Integer code;


    StatusEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }
}
