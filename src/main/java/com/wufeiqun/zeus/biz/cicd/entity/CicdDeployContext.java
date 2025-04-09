package com.wufeiqun.zeus.biz.cicd.entity;

import com.wufeiqun.zeus.dao.ApplicationDeployConfig;
import com.wufeiqun.zeus.dao.CicdBuildRecord;
import lombok.Data;
import lombok.ToString;


/**
 * 预处理, 把流程中需要的数据封装并且提前放到一个上下文环境中, 构建上下文, 可扩展
 */
@Data
@ToString
public class CicdDeployContext {

    /**
     * 前端入参form
     */
    private CicdDeployForm.RunDeployForm runDeployForm;

    /**
     * 构建记录
     */
    private CicdBuildRecord buildRecord;

    /**
     * 创建者
     */
    private String operator;

    /**
     * 发布记录
     */
    private Long deployRecordId;

    private ApplicationDeployConfig applicationDeployConfig;
    /**
     * 如果是容器部署的话, 运维平台会生成deployment yaml文件给到异步任务去使用
     */
    private String deploymentYaml;
    /**
     * 如果是容器部署的话, 运维平台会生成service yaml文件给到异步任务去使用
     */
    private String serviceYaml;
}
