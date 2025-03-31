package com.wufeiqun.zeus.service;

import com.wufeiqun.zeus.dao.Menu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wufeiqun.zeus.dao.Role;

import java.util.List;

/**
 * <p>
 * 资源表 服务类
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
public interface IMenuService extends IService<Menu> {
    List<Role> getMenuRoleList(Long menuId);

}
