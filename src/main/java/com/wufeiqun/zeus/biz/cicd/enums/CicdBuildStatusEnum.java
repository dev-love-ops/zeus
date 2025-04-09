package com.wufeiqun.zeus.biz.cicd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 构建状态枚举
 */
@Getter
public enum CicdBuildStatusEnum {
    BUILDING(0,"构建中"),
    SUCCESS(1,"构建成功"),
    FAILED(2,"构建失败"),
    STOPPED(3,"构建终止"),
    TIMEOUT(4,"构建超时");

    private final Integer code;
    private final String value;

    private static final Map<Integer,String> code2DescMap;
    static{
        code2DescMap = new HashMap<>(CicdBuildStatusEnum.values().length);
        for (CicdBuildStatusEnum buildStatus : CicdBuildStatusEnum.values()) {
            code2DescMap.put(buildStatus.getCode(), buildStatus.getValue());
        }
    }

    CicdBuildStatusEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String descWithCode(Integer code) {
        return code2DescMap.get(code);
    }
}
