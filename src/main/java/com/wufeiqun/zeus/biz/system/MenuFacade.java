package com.wufeiqun.zeus.biz.system;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wufeiqun.zeus.biz.system.entity.MenuForm;
import com.wufeiqun.zeus.biz.system.entity.MenuVO;
import com.wufeiqun.zeus.biz.system.enums.MenuTypeEnum;
import com.wufeiqun.zeus.common.exception.ServiceException;
import com.wufeiqun.zeus.dao.Menu;
import com.wufeiqun.zeus.dao.Role;
import com.wufeiqun.zeus.service.IMenuService;
import com.wufeiqun.zeus.service.IRoleMenuRelationService;
import com.wufeiqun.zeus.service.IRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wufeiqun
 * @date 2022-09-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuFacade {
    private final IMenuService menuService;
    private final MenuAdapter menuAdapter;
    private final IRoleMenuRelationService roleMenuRelationService;
    private final IRoleService roleService;


    public List<MenuVO> getMenuTreeList() {
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("sort");
        List<Menu> menuList = menuService.list(queryWrapper);
        return menuAdapter.createMenuVO(menuList);
    }

    public void createMenu(MenuForm.UpdateMenuForm form, String operator){
        // 类型验证, 1: 顶级类型必须为菜单 2: 按钮类型不支持添加子菜单
        if (Objects.isNull(form.getParentId()) || form.getParentId() == 0){
            if (!MenuTypeEnum.MENU.getCode().equals(form.getType())){
                throw new ServiceException("顶级必须是菜单类型!");
            }
        }
        Menu parent = null;
        if (Objects.nonNull(form.getParentId()) && form.getParentId() != 0){
            parent = menuService.getById(form.getParentId());
        }
        if (Objects.nonNull(parent)){
            if (!MenuTypeEnum.MENU.getCode().equals(parent.getType())){
                throw new ServiceException("非菜单类型不支持添加子菜单!");
            }
        }
        Menu menu = menuAdapter.convertToMenu(form, operator);
        menuService.save(menu);
        menuAdapter.resolveFullIdPath(menu, parent);
        menuService.updateById(menu);
    }

    public void updateMenu(MenuForm.UpdateMenuForm form, String operator){
        // 类型验证, 1: 顶级类型必须为菜单 2: 按钮类型不支持添加子菜单
        if (Objects.isNull(form.getParentId()) || form.getParentId() == 0){
            if (!MenuTypeEnum.MENU.getCode().equals(form.getType())){
                throw new ServiceException("顶级必须是菜单类型!");
            }
        }
        Menu parent = null;
        if (Objects.nonNull(form.getParentId()) && form.getParentId() != 0){
            parent = menuService.getById(form.getParentId());
        }
        if (Objects.nonNull(parent)){
            if (!MenuTypeEnum.MENU.getCode().equals(parent.getType())){
                throw new ServiceException("非菜单类型不支持添加子菜单!");
            }
        }
        Menu menu = menuAdapter.convertToMenu(form, operator);
        menuService.updateById(menu);
    }

    public void  deleteMenu(Menu menu, String operator) {
        // 检查是否从所有角色上解除绑定
        List<Role> roleList = menuService.getMenuRoleList(menu.getId());
        if (CollectionUtil.isNotEmpty(roleList)){
            String role = roleList.stream().map(Role::getName).collect(Collectors.joining(","));
            throw new ServiceException(String.format("请先从角色[%s]上解绑该菜单!", role));
        }
        menuService.removeById(menu.getId());
        log.info("用户[{}]删除菜单: {}", operator, JSON.toJSONString(menu));
    }
}
