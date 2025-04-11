package com.wufeiqun.zeus.biz.system;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wufeiqun.zeus.biz.system.entity.MenuForm;
import com.wufeiqun.zeus.biz.system.entity.MenuVO;
import com.wufeiqun.zeus.biz.system.entity.UserMenuContext;
import com.wufeiqun.zeus.biz.system.enums.MenuTypeEnum;
import com.wufeiqun.zeus.dao.Menu;
import com.wufeiqun.zeus.service.IMenuService;
import com.wufeiqun.zeus.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.wufeiqun.zeus.common.constant.GlobalConstant.SUPER_ADMIN;


/**
 * @author wufeiqun
 * @date 2022-09-05
 */
@Service
@RequiredArgsConstructor
public class MenuAdapter {
    private final IMenuService menuService;
    private final IUserService userService;



    public UserMenuContext createUserMenuContext(String account){
        UserMenuContext context = new UserMenuContext(account);
        saveUserMenuToContext(context);
        return context;
    }

    private void saveUserMenuToContext(UserMenuContext context){
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("sort");
        if (SUPER_ADMIN.equals(context.getAccount())){
            context.setMenuList(menuService.list(queryWrapper).stream()
                    .filter(menu -> MenuTypeEnum.MENU.getCode().equals(menu.getType())).collect(Collectors.toList()));
        } else {
            Set<Long> menuIdList =  userService.getUserMenuIdList(context.getAccount());
            queryWrapper.in("id", menuIdList);
            List<Menu> menuList = menuService.list(queryWrapper).stream()
                    .filter(menu -> MenuTypeEnum.MENU.getCode().equals(menu.getType())).collect(Collectors.toList());
            context.setMenuList(menuList);
        }
    }

    public List<MenuVO> createMenuVO(List<Menu> menuList){
        Map<Long, MenuVO> menuVOMap = new HashMap<>(menuList.size());
        List<MenuVO> menuVOList = new ArrayList<>(menuList.size());

        for (Menu menu : menuList) {
            MenuVO vo = createMenuVO(menu);
            menuVOMap.put(vo.getId(), vo);
            menuVOList.add(vo);
        }

        List<MenuVO> tree = new ArrayList<>(menuList.size());

        for (MenuVO item : menuVOList) {
            MenuVO parent = menuVOMap.get(item.getParentId());
            // 如果父级不为空, 把孩子添加到父级下面, 菜单类型的组装成树, 按钮类型的放到一个set中
            if (Objects.nonNull(parent)) {
                // 该树用于菜单的编辑, 所以不论什么类型都会进行树的制作
                parent.getChildren().add(item);
            } else {
                // 没有父级的肯定是menu类型, 直接加入到tree中
                tree.add(item);
            }
        }

        return tree;
    }

    private MenuVO createMenuVO (Menu menu){
        MenuVO vo = new MenuVO();
        BeanUtil.copyProperties(menu, vo);
        // 其它特殊转换放到下面
        vo.setChildren(new ArrayList<>());
        return vo;
    }

    public Menu convertToMenu (MenuForm.UpdateMenuForm form, String operator){
        Menu menu = new Menu();
        BeanUtil.copyProperties(form, menu);
        menu.setUpdateUser(operator);
        return menu;
    }

    public void resolveFullIdPath(Menu menu, Menu parentMenu) {
        String fullPath = menu.getId() + "/";
        if (Objects.nonNull(parentMenu)) {
            fullPath = parentMenu.getFullPath() + fullPath;
        } else {
            fullPath = "/" + fullPath;
        }
        menu.setFullPath(fullPath);
    }
}
