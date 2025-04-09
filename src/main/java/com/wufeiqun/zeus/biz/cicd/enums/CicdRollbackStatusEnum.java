package com.wufeiqun.zeus.biz.cicd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 构建状态枚举
 */
@Getter
public enum CicdRollbackStatusEnum {
    DEPLOYING(0,"回滚中"),
    SUCCESS(1,"回滚成功"),
    FAILED(2,"回滚失败");

    private final Integer code;
    private final String value;

    private static final Map<Integer,String> code2DescMap;
    static{
        code2DescMap = new HashMap<>(CicdRollbackStatusEnum.values().length);
        for (CicdRollbackStatusEnum buildStatus : CicdRollbackStatusEnum.values()) {
            code2DescMap.put(buildStatus.getCode(),buildStatus.getValue());
        }
    }

    CicdRollbackStatusEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String descWithCode(int code) {
        return code2DescMap.get(code);
    }
}
