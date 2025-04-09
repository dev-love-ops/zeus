package com.wufeiqun.zeus.biz.celery.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @author wufeiqun
 * @date 2022-08-18
 */
@Data
@ToString
public class CeleryCicdBuildForm {
    private String appCode;
    private Long buildRecordId;
    private String envCode;
    private String git;
    private String branch;
    private String tag;
    private String pkgType;
    private String pkgName;
    private String pkgPath;
    private String profile;
    private String buildExtraArgs;
    private String runtimeVersion;
    private Boolean containerized;
    private String runExtraArgs;
    private Boolean reInstallDependency;
    // 子模块相关配置
    private Boolean enableSubModule;
    private String subModuleGit;
    private String subModuleBranch;
    private String subModuleDirectory;
    private String dockerfileTemplateName;

}
