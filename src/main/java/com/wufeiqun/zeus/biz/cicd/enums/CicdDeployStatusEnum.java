package com.wufeiqun.zeus.biz.cicd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 构建状态枚举
 */
@Getter
public enum CicdDeployStatusEnum {
    DEPLOYING(0,"发布中"),
    SUCCESS(1,"发布成功"),
    FAILED(2,"发布失败"),
    STOPPED(3,"发布终止"),
    TIMEOUT(4,"发布超时"),
    ROLLBACK(5,"已回滚");

    private final Integer code;
    private final String value;

    private static final Map<Integer,String> code2DescMap;
    static{
        code2DescMap = new HashMap<>(CicdDeployStatusEnum.values().length);
        for (CicdDeployStatusEnum buildStatus : CicdDeployStatusEnum.values()) {
            code2DescMap.put(buildStatus.getCode(),buildStatus.getValue());
        }
    }

    CicdDeployStatusEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String descWithCode(int code) {
        return code2DescMap.get(code);
    }
}
