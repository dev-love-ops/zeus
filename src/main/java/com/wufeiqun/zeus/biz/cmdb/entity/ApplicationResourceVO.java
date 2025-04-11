package com.wufeiqun.zeus.biz.cmdb.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationResourceVO {
    private String instanceId;
    private String instanceName;
    private String ip;
    private String privateIp;
    private String comment;
    private String status;
    private LocalDateTime createTime;
}
