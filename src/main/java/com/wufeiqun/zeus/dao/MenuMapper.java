package com.wufeiqun.zeus.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;


/**
 * <p>
 * 资源表 Mapper 接口
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
public interface MenuMapper extends BaseMapper<Menu> {
    List<Role> getMenuRoleList(Long menuId);
}

