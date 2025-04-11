package com.wufeiqun.zeus.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
/**
 * <p>
 * 应用
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
@Getter
@Setter
@ToString
public class Application {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 应用code, 唯一
     */
    private String code;

    /**
     * 应用名称
     */
    private String name;

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

    /**
     * 状态, 1:在用, 0: 不在使用
     */
    private Integer status;

    /**
     * 应用所属部门
     */
    private String department;

    /**
     * 应用负责人
     */
    private String owner;

    /**
     * 应用token
     */
    private String token;

    /**
     * 应用级别, 用于监控/发布等环节, L0-L2, L0最高
     */
    private String level;
}
