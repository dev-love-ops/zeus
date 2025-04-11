package com.wufeiqun.zeus.common.utils;

import com.wufeiqun.zeus.dao.Menu;
import com.wufeiqun.zeus.service.IMenuService;
import com.wufeiqun.zeus.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import static com.wufeiqun.zeus.common.constant.GlobalConstant.SUPER_ADMIN;


/**
 * @author wufeiqun
 * @date 2022-09-21
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionUtil {

    private final IMenuService menuService;
    private final IUserService userService;


    /**
     * 查询用户的权限编码, 用于前端/后端接口的校验
     * TODO: 这里可以加缓存/删除缓存等
     */
    public Set<String> getUserPermissionCodeList(String account) {
        if (SUPER_ADMIN.equals(account)){
            return menuService.list().stream().map(Menu::getCode).collect(Collectors.toSet());
        }
        return userService.getUserPermissionCodeList(account);
    }

    public boolean noPermission(String account, String permission) {
        Set<String> permissionCodeList = getUserPermissionCodeList(account);
        return !permissionCodeList.contains(permission);
    }
}
