package com.wufeiqun.zeus.common.entity;


import lombok.Data;

@Data
public class WebLog {
    /**
     * 操作描述
     */
    public String description;

    /**
     * 操作用户
     */
    public String username;

    /**
     * 操作时间
     */
    public Long startTime;

    /**
     * 消耗时间
     */
    public Integer spendTime;

    /**
     * 根路径
     */
    public String basePath;

    /**
     * URI
     */
    public String uri;

    /**
     * URL
     */
    public String url;

    /**
     * 请求类型
     */
    public String method;

    /**
     * IP地址
     */
    public String ip;

    public Object parameter;

    public Object result;

}
