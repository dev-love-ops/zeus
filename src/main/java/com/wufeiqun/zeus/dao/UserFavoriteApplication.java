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
 * 用户收藏的应用
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
@Getter
@Setter
@ToString
@TableName("user_favorite_application")
public class UserFavoriteApplication {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 应用编码
     */
    private String appCode;

    /**
     * 创建用户
     */
    private String user;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
