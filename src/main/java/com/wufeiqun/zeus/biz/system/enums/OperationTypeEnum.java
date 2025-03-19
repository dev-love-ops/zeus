package com.wufeiqun.zeus.biz.system.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 操作类型枚举
 */
@Getter
public enum OperationTypeEnum {
    UPDATE_APPLICATION_DEPLOY_CONFIG("UPDATE_APPLICATION_DEPLOY_CONFIG","更新应用部署配置"),
    API("","");

    private final String type;
    private final String desc;

    private static final Map<String,String> code2DescMap;
    static{
        code2DescMap = new HashMap<>(OperationTypeEnum.values().length);
        for (OperationTypeEnum buildStatus : OperationTypeEnum.values()) {
            code2DescMap.put(buildStatus.getType(),buildStatus.getDesc());
        }
    }

    OperationTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static String descWithCode(String type) {
        return code2DescMap.get(type);
    }
}
