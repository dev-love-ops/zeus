package com.wufeiqun.zeus.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
/**
 * <p>
 * 应用发布相关配置
 * </p>
 *
 * @author wufeiqun
 * @since 2025-04-03
 */
@Getter
@Setter
@ToString
@TableName("application_deploy_config")
public class ApplicationDeployConfig {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 应用code, 唯一
     */
    private String appCode;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建用户
     */
    private String createUser;

    /**
     * 更新用户
     */
    private String updateUser;

    /**
     * gitlab代码仓库地址
     */
    private String git;

    /**
     * 健康检查路径
     */
    private String healthCheckUri;

    /**
     * 制品名称
     */
    private String artifactName;

    /**
     * 制品类型
     */
    private String artifactType;

    /**
     * 制品路径
     */
    private String artifactPath;

    /**
     * 服务监听端口
     */
    private Integer port;

    /**
     * 应用类型
     */
    private String type;

    /**
     * 探活方式, 支持HTTP/TCP/LOG/CUSTOM等
     */
    private String probeType;

    /**
     * 自定义编译参数
     */
    private String buildExtraArgs;

    /**
     * 环境, 每个应用不同环境可能有不同的配置
     */
    private String env;

    /**
     * mvn打包的时候可能会用到
     */
    private String profile;

    /**
     * 构建分支, 只有在快速发布模式下生效
     */
    private String buildBranch;

    /**
     * 企业微信机器人
     */
    private String workWeixinToken;

    /**
     * 初始启动延迟时间
     */
    private Integer initialDelaySeconds;

    /**
     * git提交后自动发布
     */
    private Boolean autoDeployOnGitCommit;

    /**
     * 运行时版本号, 包括构建和运行
     */
    private String runtimeVersion;

    /**
     * 是否已经容器化
     */
    private Boolean containerized;

    /**
     * 程序启动额外参数
     */
    private String runExtraArgs;

    /**
     * 命名空间
     */
    private String kubernetesNamespace;

    /**
     * 容器调度策略
     */
    private String kubernetesScheduleStrategy;

    /**
     * 副本数
     */
    private Integer kubernetesReplicas;

    /**
     * CPU限制
     */
    private Integer kubernetesLimitCpu;

    /**
     * 内存限制, 单位MI
     */
    private Integer kubernetesLimitMemory;

    /**
     * 指定应用service的node port
     */
    private Integer kubernetesNodePort;

    /**
     * 是否开启prometheus抓取(容器专用), 默认不开启
     */
    private Boolean prometheusScrape;

    /**
     * prometheus抓取路径, 默认为/actuator/prometheus
     */
    private String prometheusPath;

    /**
     * 容器化的时候使用的Dockerfile模板名称
     */
    private String dockerfileTemplateName;
}
