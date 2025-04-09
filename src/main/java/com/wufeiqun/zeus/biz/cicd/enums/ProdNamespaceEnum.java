package com.wufeiqun.zeus.biz.cicd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 生产命名空间枚举, 目前按照每个团队一个, 团队级别的资源隔离
 */
@Getter
public enum ProdNamespaceEnum {
    BACKEND("backend","后端团队"),
    FRONTEND("frontend","前端团队"),
    ARCH("architecture","架构团队"),
    SRE("sre","运维团队"),
    SPIDER("spider","爬虫团队"),
    QA("qa","测试团队"),
    BIGDATA("bigdata","大数据团队");

    private final String namespace;
    private final String desc;

    private static final Map<String, String> code2DescMap;
    static{
        code2DescMap = new HashMap<>(ProdNamespaceEnum.values().length);
        for (ProdNamespaceEnum e : ProdNamespaceEnum.values()) {
            code2DescMap.put(e.getNamespace(), e.getDesc());
        }
    }

    ProdNamespaceEnum(String namespace, String desc) {
        this.namespace = namespace;
        this.desc = desc;
    }

    public static String descWithCode(String namespace) {
        return code2DescMap.get(namespace);
    }
}
