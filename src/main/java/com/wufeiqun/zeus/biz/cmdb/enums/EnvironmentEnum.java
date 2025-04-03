package com.wufeiqun.zeus.biz.cmdb.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wufeiqun
 * @date 2022-07-12
 * 环境枚举, 一般环境也是固定的, 不怎么变化的, 只是在开始阶段跟大家共识一下
 * 每个环境不一定都是必要的, 根据具体项目的需要而使用即可
 */
@Getter
public enum EnvironmentEnum {
    /**
     * 生产环境
     */
    PROD("PROD", "生产环境", "正式环境"),

    /**
     * 预发环境
     */
    PRE("PRE", "预发环境", "数据跟生产数据相同, 也可以采用不单独搞一套环境的搭建, 而是在生产环境中配置特殊规则 比如公司出口IP作为灰度验证, 比如把公司的内部员工账号加入某些规则来验证等"),

    /**
     * 测试环境
     */
    TEST("TEST", "测试环境", "用于QA对项目发布前的验收 QA主导, 研发配合");

    private final String name;
    private final String code;
    private final String comment;


    private static final Map<String,String> codeToNameMap;
    static{
        codeToNameMap = new HashMap<>(EnvironmentEnum.values().length);
        for (EnvironmentEnum env : EnvironmentEnum.values()) {
            codeToNameMap.put(env.getCode(), env.getName());
        }
    }

    EnvironmentEnum(String code, String name, String comment) {
        this.code = code;
        this.name = name;
        this.comment = comment;
    }

    public static String descWithCode(String code) {
        return codeToNameMap.get(code);
    }
}
