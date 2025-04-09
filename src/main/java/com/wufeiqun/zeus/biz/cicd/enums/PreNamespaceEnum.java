package com.wufeiqun.zeus.biz.cicd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 预发命名空间枚举, 目前按照每个团队一个, 团队级别的资源隔离
 */
@Getter
public enum PreNamespaceEnum {
    DEFAULT("pre","预发环境");

    private final String namespace;
    private final String desc;

    private static final Map<String, String> code2DescMap;
    static{
        code2DescMap = new HashMap<>(PreNamespaceEnum.values().length);
        for (PreNamespaceEnum e : PreNamespaceEnum.values()) {
            code2DescMap.put(e.getNamespace(), e.getDesc());
        }
    }

    PreNamespaceEnum(String namespace, String desc) {
        this.namespace = namespace;
        this.desc = desc;
    }

    public static String descWithCode(String namespace) {
        return code2DescMap.get(namespace);
    }
}
