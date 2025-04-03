package com.wufeiqun.zeus.biz.cmdb.entity;

import lombok.Data;

import java.util.List;

public class ApplicationConfigForm {
    @Data
    public static class ApplicationDeployConfigForm {
        private String appCode;
        /**
         * 通用配置, 不区分环境
         */
        private ApplicationDeployConfigCommon common;
        /**
         * 生产环境
         */
        private ApplicationDeployConfigByEnv prod;
        /**
         * 预发环境
         */
        private ApplicationDeployConfigByEnv pre;
        /**
         * 测试环境
         */
        private ApplicationDeployConfigByEnv test;
    }

    /**
     * 应用通用的发布配置, 比如git地址等
     */
    @Data
    public static class ApplicationDeployConfigCommon {
        /**
         * 项目的gitlab地址
         */
        private String git;
        /**
         * 制品名称, 比如 `pharmcube-invest.jar`
         */
        private String artifactName;
        /**
         * 制品类型, 不同制品类型会走不同的构建发布逻辑
         */
        private String artifactType;
        /**
         * 制品相对路径, 比如SpringBoot项目一般为 `/xxx/targets`
         */
        private String artifactPath;
        /**
         * 探活方式
         */
        private String probeType;
        /**
         * 探活接口URI, 只有在探活方式为HTTP的时候生效
         */
        private String healthCheckUri;

        /**
         * 企业微信机器人, 用于通知
         */
        private String workWeixinToken;
        /**
         * 程序启动延迟时间, 超过这个时间以后才开始探活等
         */
        private Integer initialDelaySeconds;
        /**
         * git提交后自动触发构建发布, 通过gitlab的webhook监听, 这个功能要谨慎使用
         * 目前仅用于测试环境, 实验性开放
         */
        private Boolean autoDeployOnGitCommit;
        /**
         * 运行时版本
         */
        private String runtimeVersion;

        /**
         * 是否容器化, 只有开启容器化, 容器相关的功能才会执行, 比如构建docker镜像等
         */
        private Boolean containerized;
    }

    @Data
    public static class ApplicationDeployConfigByEnv {
        /**
         * port
         */
        private Integer port;
        /**
         * 编译参数
         */
        private String buildExtraArgs;
        /**
         * 构建分支, 只在 快速发布 的时候会用到
         */
        private String buildBranch;
        /**
         * 负载类型, 发布的时候自动摘挂流量, 格式为: dubbo|nginx|xxx
         */
        private List<String> loadType;
        /**
         * 激活的profile, 比如 mvn -P {profile}
         */
        private String profile;
        /**
         * 程序运行的额外参数. 目前容器化生成Dockerfile会用到
         */
        private String runExtraArgs;
        private String kubernetesNamespace;
        /**
         * 应用容器化的时候使用的dockerfile模板, 一般应用用不到, 特殊的会自己制定一个镜像
         */
        private String dockerfileTemplateName;
        private Integer kubernetesReplicas;
        private Integer kubernetesLimitCpu;
        private Integer kubernetesLimitMemory;
        private Integer kubernetesNodePort;
        private String kubernetesScheduleStrategy;
        private Boolean prometheusScrape;
        private String prometheusPath;
    }
}
