package com.wufeiqun.zeus.common.constant;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author 吴飞群
 * @createTime 2022/05/17
 */
public class GlobalConstant {
    public static final String REQUEST_USER_KEY = "user";
    public static final String JWT_TOKEN_SECRET = "zeus";
    /**
     * 由于采用的后端权限校验/后端会控制菜单的列表, 为了防止自己把自己给玩进去, 也就是
     * 自己把自己的菜单给去掉了, 导致不好恢复, 所以搞了一个超管, 超管拥有所有权限, 一般不会登录使用
     * 只是在紧急情况下临时使用
     */
    public static final String SUPER_ADMIN = "admin";
    /**
     * 运维报警群机器人
     */
    public static final String ZEUS_WORK_WECHAT_ROBOT_KEY = "";

    /**
     * appCode命名前缀, 原因如下:
     * 1. 后端应用的appCode会自动生成相应的ES日志索引, ES是共用的, 索引模板的适用规则配置的这些固定的前缀
     * 2. 防止新手随意命名
     */
    public static final HashSet<String> CMDB_APPCODE_PREFIX =
            new HashSet<>(Arrays.asList("fe", "web"));
}
