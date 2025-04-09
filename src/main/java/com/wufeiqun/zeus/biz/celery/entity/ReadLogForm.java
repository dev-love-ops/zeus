package com.wufeiqun.zeus.biz.celery.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @author wufeiqun
 * @date 2022-08-18
 */
@Data
@ToString
public class ReadLogForm {

    /**
     * 唯一日志ID, 比如构建日志的ID就是 `buildRecordId`
     */
    private String id;

    /**
     * 参考 zeus.celery.enums.CeleryNamespaceEnum
     */
    private String action;

    /**
     * 日志偏移量
     */
    private Long offset;
}