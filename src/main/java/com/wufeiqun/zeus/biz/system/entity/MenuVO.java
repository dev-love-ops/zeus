package com.wufeiqun.zeus.biz.system.entity;

import lombok.Data;

import java.util.List;

/**
 * @author wufeiqun
 * @date 2022-09-16
 */
@Data
public class MenuVO {

        private Long id;
        /**
         * 该资源的父级资源ID
         */
        private Long parentId;
        /**
         * 资源类型
         */
        private Integer type;
        /**
         * 资源编码, 全局唯一
         */
        private String code;
        /**
         * 资源名称
         */
        private String name;
        /**
         * 请求后端的URL路径
         */
        private String url;
        /**
         * 图标, 前端会用到
         */
        private String icon;
        /**
         * 用户访问的前端路径, 前端会用到
         */
        private String path;
        /**
         * 资源排序, 前端会用到
         */
        private Integer sort;
        /**
         * 是否隐藏, 1: 隐藏, 0: 显示
         */
        private Integer hide;
        /**
         * TAB名称
         */
        private String tabName;
        /**
         * 组件名称, 比如 `LAYOUT`
         */
        private String component;
        /**
         * 跳转路径, 一级菜单会用到
         */
        private String redirect;
        /**
         * 隐藏菜单激活的TAB
         */
        private String currentActiveMenu;
        /**
         * 生产菜单树的时候会用到, 菜单会放到这里
         */
        private List<MenuVO> children;

}
