package com.wufeiqun.zeus.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 * 发布记录表
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
@Getter
@Setter
@ToString
@TableName("cicd_deploy_record")
public class CicdDeployRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 构建记录ID
     */
    private Long buildRecordId;

    /**
     * 发布状态
     */
    private Integer deployStatus;

    /**
     * 应用code
     */
    private String appCode;

    /**
     * 环境code
     */
    private String envCode;

    /**
     * 机器IP
     */
    private String serverIp;

    /**
     * 是否回滚
     */
    private Boolean rollback;

    /**
     * 是否为docker发布
     */
    private Boolean dockerDeploy;

    /**
     * 发布模式
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

    private String celeryTaskId;
}
