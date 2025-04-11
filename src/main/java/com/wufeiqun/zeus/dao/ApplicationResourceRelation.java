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
 * 应用资源关系
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
@Getter
@Setter
@ToString
@TableName("application_resource_relation")
public class ApplicationResourceRelation {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 应用code
     */
    private String appCode;

    /**
     * 环境code
     */
    private String env;

    /**
     * 资源类型, 看代码资源类型枚举
     */
    private String resourceType;

    /**
     * 资源的唯一标识符
     */
    private String resourceId;

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
