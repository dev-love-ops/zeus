package com.wufeiqun.zeus.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
/**
 * <p>
 * 部门
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
@Getter
@Setter
@ToString
public class Department {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 部门编码, 唯一
     */
    private String code;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 负责人账号
     */
    private String leaderAccount;

    /**
     * 父级部门code
     */
    private String parentCode;

    /**
     * 部门编码全路径, 包含自身
     */
    private String fullDepartmentCode;

    /**
     * 部门名称全路径
     */
    private String fullDepartmentName;

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
}
