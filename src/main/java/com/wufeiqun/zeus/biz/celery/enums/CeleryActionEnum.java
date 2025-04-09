package com.wufeiqun.zeus.biz.celery.enums;

import lombok.Getter;

/**
 * 异步任务以后会对接很多任务, 不只是构建发布, 所以任务之间的很多地方需要隔离
 * 比如说需要前端显示的日志路径, logger/thread等
 */
@Getter
public enum CeleryActionEnum {
    /**
     * 构建
     */
    BUILD("build"),
    /**
     * 发布
     */
    DEPLOY("deploy"),
    /**
     * 发布
     */
    ROLLBACK("rollback"),
    /**
     * 重启
     */
    RESTART("restart");
    private final String value;

    CeleryActionEnum(String value) {
        this.value = value;
    }
}
