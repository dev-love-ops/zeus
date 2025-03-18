package com.wufeiqun.zeus.controller.system;

import com.wufeiqun.zeus.biz.system.UserFacade;
import com.wufeiqun.zeus.biz.system.entity.UserForm;
import com.wufeiqun.zeus.common.entity.CommonVo;
import com.wufeiqun.zeus.common.utils.RequestUtil;
import com.wufeiqun.zeus.dao.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/system/user/")
public class UserManagementController {

    private final UserFacade userFacade;

    @GetMapping("/getSelectableUserList")
    public CommonVo<Object> getSelectableUserList() {
        return CommonVo.success(userFacade.getSelectableUserList());
    }

    @PostMapping("/getPageableUserList")
    public CommonVo<Object> getPageableUserList(@RequestBody UserForm.UserSearchForm form) {
        return CommonVo.success(userFacade.getPageableUserList(form));
    }

    @PostMapping("/updateUserRoleRelation")
    public CommonVo<Object> updateUserRoleRelation(@RequestBody  @Valid UserForm.UpdateUserForm form) {
        User user = RequestUtil.getCurrentUser();
        userFacade.updateUserRoleRelation(form, user.getAccount());
        return CommonVo.success();
    }


}
