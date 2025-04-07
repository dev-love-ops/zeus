package com.wufeiqun.zeus.common.constant;

import cn.hutool.core.util.RandomUtil;

/**
 * @author wufeiqun
 * @date 2022-08-29
 */
public class RedisCacheKey {
    public static Long GLOBAL_CACHE_TTL(){
        return 7 * 24 * 60 * 60L + RandomUtil.randomInt(10*60, 60*60);
    }

    public static Long CACHE_TTL_ONE_HOUR = 60 * 60L;
    public static final String PROJECT_PREFIX = "ZEUS_";
    /**
     * 根据用户account查询用户信息
     */
    public static final String USER_QUERY_USER_BY_ACCOUNT = PROJECT_PREFIX + "USER_QUERY_USER_BY_ACCOUNT_";
    public static final String USER_QUERY_USER_BY_WORKWX = PROJECT_PREFIX + "USER_QUERY_USER_BY_WORKWX_";
    public static final String WORKWX_ACCESS_TOKEN = "WORKWX_ACCESS_TOKEN";
    public static final String CICD_RATE_LIMIT_BUILD = "CICD_RATE_LIMIT_BUILD";
    public static final String CICD_RATE_LIMIT_DEPLOY = "CICD_RATE_LIMIT_DEPLOY";

}
