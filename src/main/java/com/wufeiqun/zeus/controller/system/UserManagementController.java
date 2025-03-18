package com.wufeiqun.zeus.controller.system;

import com.wufeiqun.zeus.biz.system.UserFacade;
import com.wufeiqun.zeus.common.entity.CommonVo;
import com.wufeiqun.zeus.common.entity.SelectVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

//    @PostMapping("/getUserList")
//    public CommonVo<Object> getPageableUserList(@RequestBody UserForms.UserSearchForm form) {
//        return CommonVo.success(userFacade.getPageableUserList(form));
//    }
//
//    @PostMapping("/updateUserRoleRelation")
//    public CommonVo<Object> updateUserRoleRelation(@RequestBody  @Valid UserForms.UpdateUserForm form) {
//        User user = RequestUtil.getCurrentUser();
//        userFacade.update(form);
//        userFacade.updateUserRoleRelation(form, user.getAccount());
//        return CommonVo.success();
//    }


}
