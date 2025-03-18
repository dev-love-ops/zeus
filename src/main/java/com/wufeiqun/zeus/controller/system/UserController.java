package com.wufeiqun.zeus.controller.system;

import cn.hutool.core.bean.BeanUtil;
import com.wufeiqun.zeus.biz.system.UserFacade;
import com.wufeiqun.zeus.biz.system.entity.UserInfo;
import com.wufeiqun.zeus.common.entity.CommonVo;
import com.wufeiqun.zeus.common.utils.RequestUtil;
import com.wufeiqun.zeus.dao.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 当前登录用户的一些逻辑
 */
@Slf4j
@RestController
@RequestMapping("/api/user/")
@RequiredArgsConstructor
public class UserController {

    private final UserFacade userFacade;


    @GetMapping("/getUserInfo")
    public CommonVo<UserInfo> getUserInfo() {
        User user = RequestUtil.getCurrentUser();

        UserInfo userInfo = new UserInfo();
        BeanUtil.copyProperties(user, userInfo);
        userInfo.setPassword("");
        userInfo.setAvatar("https://q1.qlogo.cn/g?b=qq&nk=190848757&s=640");
        userInfo.setHomePath("/cmdb/application");

        userInfo.setPermissionCodeList(userFacade.getUserPermissionCodeList(user.getAccount()));

        return CommonVo.success(userInfo);
    }

    @GetMapping("/getUserMenuList")
    public CommonVo<Object> getUserMenuList() {
        User user = RequestUtil.getCurrentUser();
        return CommonVo.success();
//        return CommonVo.success(userFacade.getUserMenuList(user.getAccount()));
    }

//    @PostMapping("/changePassword")
//    public CommonVo<Object> login(@RequestBody @Valid UserForms.ChangePasswordForm form){
//        User user = RequestUtil.getCurrentUser();
//        userFacade.changePassword(form, user.getAccount());
//        return CommonVo.success();
//    }

}
