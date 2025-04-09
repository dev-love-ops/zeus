package com.wufeiqun.zeus.biz.celery.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author wufeiqun
 * @date 2022-08-18
 */
@Data
@ToString
public class CeleryCicdDeployForm {
    private String appCode;
    private String envCode;
    private String serverIp;
    private Integer httpPort;
    private String pkgUrl;
    private String pkgType;
    private String pkgName;
    private String pkgPath;
    private Long deployRecordId;
    private List<String> loadTypeList;
    private String probeType;
    private String healthCheckUri;
    private Integer initialDelaySeconds;
    private Boolean dockerMode;
    private String deploymentYaml;
    private String serviceYaml;
    private String namespace;
    private Boolean isRollback;
}
