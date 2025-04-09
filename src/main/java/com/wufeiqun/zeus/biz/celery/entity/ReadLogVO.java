package com.wufeiqun.zeus.biz.celery.entity;

import lombok.Data;

/**
 * @author wufeiqun
 * @date 2022-08-18
 */
@Data
public class ReadLogVO {

    /**
     * 日志内容
     */
    private String logContent;

    /**
     * 日志偏移量
     */
    private long offset;

    /**
     * 日志完成状态
     */
    private boolean completeFlag;
}