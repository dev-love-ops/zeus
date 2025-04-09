package com.wufeiqun.zeus.biz.cicd.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * kubernetes调度策略枚举
 */
@Getter
public enum ScheduleStrategyEnum {
    ONLINE("ONLINE","核心在线", "重要的对外web服务, 只有该类型的服务会单独部署在一批集群节点, 其它类型的服务会部署在其它的节点"),
    OFFLINE("OFFLINE","离线任务", "如上"),
    INNER("INNER","内部服务", "如上"),
    OTHER("OTHER","其它服务", "如上");

    private final String code;
    private final String name;
    private final String desc;

    private static final Map<String, String> codeToNameMap;
    static{
        codeToNameMap = new HashMap<>(ScheduleStrategyEnum.values().length);
        for (ScheduleStrategyEnum e : ScheduleStrategyEnum.values()) {
            codeToNameMap.put(e.getCode(), e.getName());
        }
    }

    ScheduleStrategyEnum(String code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public static String getNameByCode(String code) {
        return codeToNameMap.get(code);
    }
}
