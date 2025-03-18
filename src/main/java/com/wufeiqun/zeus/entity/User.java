package com.wufeiqun.zeus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 * 用户
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-17
 */
@Getter
@Setter
@ToString
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户账号, 唯一
     */
    private String account;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 部门编码
     */
    private String departmentCode;

    /**
     * 用户类型, 默认为0, 表示内部员工, 其它的备用
     */
    private Integer type;

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
     * 用户状态, 1: 在用, 0: 禁用
     */
    private Boolean status;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户关联的企业微信ID, 用于发送通知
     */
    private String workwxId;
}
