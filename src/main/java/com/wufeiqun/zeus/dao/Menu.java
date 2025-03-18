package com.wufeiqun.zeus.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 * 资源表
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
@Getter
@Setter
@ToString
public class Menu {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父资源ID
     */
    private Long parentId;

    /**
     * 资源类型(菜单资源,按钮资源,数据资源)
     */
    private Integer type;

    /**
     * 资源名称
     */
    private String name;

    /**
     * 资源Code,用户填入,系统内唯一
     */
    private String code;

    /**
     * 资源级别全路径
     */
    private String fullPath;

    /**
     * 是否隐藏
     */
    private Integer hide;

    /**
     * 前端路径
     */
    private String path;

    /**
     * 请求url
     */
    private String url;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 前端图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * Tab选项卡路径名称
     */
    private String tabName;

    /**
     * 页面组件
     */
    private String component;

    /**
     * 跳转链接
     */
    private String redirect;

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
     * 隐藏菜单激活的TAB
     */
    private String currentActiveMenu;
}
