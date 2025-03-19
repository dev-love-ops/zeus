package com.wufeiqun.zeus.biz.system.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 菜单类型枚举
 */
@Getter
public enum MenuTypeEnum {
    MENU(0,"菜单"),
    BUTTON(1,"按钮"),
    API(2,"接口");

    private final Integer code;
    private final String desc;

    private static final Map<Integer,String> code2DescMap;
    static{
        code2DescMap = new HashMap<>(MenuTypeEnum.values().length);
        for (MenuTypeEnum buildStatus : MenuTypeEnum.values()) {
            code2DescMap.put(buildStatus.getCode(),buildStatus.getDesc());
        }
    }

    MenuTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String descWithCode(Integer code) {
        return code2DescMap.get(code);
    }
}
