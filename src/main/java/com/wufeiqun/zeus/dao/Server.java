package com.wufeiqun.zeus.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
/**
 * <p>
 * 服务器
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
@Getter
@Setter
@ToString
public class Server {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 实例ID, 对应云厂商的唯一标志的ID
     */
    private String instanceId;

    /**
     * 实例名称
     */
    private String instanceName;

    /**
     * 公网IP
     */
    private String publicIp;

    /**
     * 内网IP
     */
    private String privateIp;

    /**
     * 云厂商
     */
    private String vendor;

    /**
     * 状态, 1:在用, 0: 不在使用
     */
    private Byte status;

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
}
