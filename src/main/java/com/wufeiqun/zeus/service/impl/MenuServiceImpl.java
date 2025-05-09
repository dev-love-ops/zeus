package com.wufeiqun.zeus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wufeiqun.zeus.dao.Menu;
import com.wufeiqun.zeus.dao.MenuMapper;
import com.wufeiqun.zeus.dao.Role;
import com.wufeiqun.zeus.service.IMenuService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 资源表 服务实现类
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    @Override
    public List<Role> getMenuRoleList(Long menuId) {
        return baseMapper.getMenuRoleList(menuId);
    }
}
