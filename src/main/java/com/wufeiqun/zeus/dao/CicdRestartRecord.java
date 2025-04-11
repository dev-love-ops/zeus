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
 * 重启记录表
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
@Getter
@Setter
@ToString
@TableName("cicd_restart_record")
public class CicdRestartRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 记录状态
     */
    private Integer status;

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
     * 是否为docker发布
     */
    private Boolean dockerMode;

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
