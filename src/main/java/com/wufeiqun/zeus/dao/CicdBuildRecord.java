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
 * 构建记录表
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
@Getter
@Setter
@ToString
@TableName("cicd_build_record")
public class CicdBuildRecord {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 构建应用
     */
    private String buildAppCode;

    /**
     * 构建分支
     */
    private String buildBranch;

    /**
     * 构建环境
     */
    private String buildEnvCode;

    /**
     * 构建耗时(秒)
     */
    private Integer buildTime;

    /**
     * 0:构建中 1:构建成功 2:构建失败 3:构建超时 4:暂停
     */
    private Integer buildStatus;

    /**
     * 构建tag
     */
    private String buildTag;

    /**
     * 构建所选择的profile
     */
    private String buildProfile;

    /**
     * 制品包url(虚拟机会用到)
     */
    private String buildTargetUrl;

    /**
     * 制品docker镜像ID
     */
    private String buildTargetImageId;

    /**
     * 发布任务ID
     */
    private Integer cicdTaskId;

    /**
     * 发布任务项ID
     */
    private Integer cicdTaskItemId;

    /**
     * 发布模式(0:任务模式 1.分支模式(快速模式)
     */
    private Integer deployMode;

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
     * 备注
     */
    private String comment;

    private String celeryTaskId;

    /**
     * harbor镜像URL
     */
    private String buildImageUrl;

    /**
     * 关联的ones Project
     */
    private String onesProject;

    /**
     * 关联的ones Sprint
     */
    private String onesSprint;

    /**
     * 打包耗时
     */
    private Integer duration;

    /**
     * 关联的ones发布任务ID
     */
    private String onesDeployTask;

    /**
     * 关联的ones的项目名称, 用于通知
     */
    private String onesProjectName;

    /**
     * 关联的ones的迭代名称, 用于通知
     */
    private String onesSprintName;

    /**
     * 关联的ones的上线任务名称, 用于通知
     */
    private String onesDeployTaskName;
}
