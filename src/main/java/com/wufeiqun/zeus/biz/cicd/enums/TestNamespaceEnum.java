package com.wufeiqun.zeus.biz.cicd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试命名空间枚举, 共用一个即可
 */
@Getter
public enum TestNamespaceEnum {
    DEFAULT("test","测试环境");

    private final String namespace;
    private final String desc;

    private static final Map<String, String> code2DescMap;
    static{
        code2DescMap = new HashMap<>(TestNamespaceEnum.values().length);
        for (TestNamespaceEnum e : TestNamespaceEnum.values()) {
            code2DescMap.put(e.getNamespace(), e.getDesc());
        }
    }

    TestNamespaceEnum(String namespace, String desc) {
        this.namespace = namespace;
        this.desc = desc;
    }

    public static String descWithCode(String namespace) {
        return code2DescMap.get(namespace);
    }
}
