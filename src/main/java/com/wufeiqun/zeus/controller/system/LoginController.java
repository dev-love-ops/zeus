package com.wufeiqun.zeus.controller.system;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.jwt.JWTUtil;
import com.wufeiqun.zeus.biz.system.UserFacade;
import com.wufeiqun.zeus.biz.system.entity.UserForm;
import com.wufeiqun.zeus.biz.system.entity.UserInfo;
import com.wufeiqun.zeus.common.constant.GlobalConstant;
import com.wufeiqun.zeus.common.entity.CommonVo;
import com.wufeiqun.zeus.common.utils.RequestUtil;
import com.wufeiqun.zeus.dao.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class LoginController {
    private final UserFacade userFacade;

    @PostMapping("/login")
    public CommonVo<Object> login(@RequestBody @Valid UserForm.LoginForm form){
        boolean status = userFacade.validateToken(form.getUsername(), form.getPassword());
        if (!status){
            return CommonVo.error("密码错误");
        }
        Map<String, String> ret = new HashMap<>();
        Map<String, Object> payload = new HashMap<>();
        payload.put("account", form.getUsername());
        payload.put("expireAt", System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7);
        ret.put("token", JWTUtil.createToken(payload, GlobalConstant.JWT_TOKEN_SECRET.getBytes()));
        return CommonVo.success(ret);
    }

    @GetMapping("/logout")
    public CommonVo<Object> logout(){
        return CommonVo.success("OK");
    }

    @GetMapping("/getUserInfo")
    public CommonVo<UserInfo> getUserInfo() {
        User user = RequestUtil.getCurrentUser();

        UserInfo userInfo = new UserInfo();
        BeanUtil.copyProperties(user, userInfo);
        userInfo.setAvatar("https://q1.qlogo.cn/g?b=qq&nk=190848757&s=640");
        userInfo.setHomePath("/cmdb/application");

        return CommonVo.success(userInfo);
    }

}
