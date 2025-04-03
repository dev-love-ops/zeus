package com.wufeiqun.zeus.biz.cicd.enums;

import lombok.Getter;


/**
 * 制品类型枚举
 */
@Getter
public enum ArtifactTypeEnum {
    JAR("jar","SpringBoot等常规的Java项目"),
    JARAPI("jar-api","API依赖包, 会推到私服"),
    STATICFE("static-fe","常规的前端项目(vue/react等)"),
    PY("python","python项目"),
    GO("golang","golang项目"),
    NODE("node","node项目"),
    STATIC("static","普通的文件");

    private final String type;
    private final String desc;

    ArtifactTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static boolean isSupportRollback(String type) {
        return JAR.getType().equals(type) || STATICFE.getType().equals(type);
    }
}
