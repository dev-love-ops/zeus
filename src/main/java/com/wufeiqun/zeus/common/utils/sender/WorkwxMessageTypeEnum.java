package com.wufeiqun.zeus.common.utils.sender;

import lombok.Getter;

/**
 * @author wufeiqun
 * @date 2022-07-12
 * 企业微信机器人消息类型枚举
 */
@Getter
public enum WorkwxMessageTypeEnum {
    /**
     * 文本类型
     */
    TEXT("文本类型", "text"),
    /**
     * markdown类型
     */
    MARKDOWN("markdown类型", "markdown");

    /**
     * 状态名称
     */
    private final String name;
    /**
     * 状态code
     */
    private final String code;


    WorkwxMessageTypeEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
