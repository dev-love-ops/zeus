package com.wufeiqun.zeus.controller.system;

import com.wufeiqun.zeus.biz.system.RoleFacade;
import com.wufeiqun.zeus.biz.system.entity.RoleForm;
import com.wufeiqun.zeus.common.entity.CommonVo;
import com.wufeiqun.zeus.common.entity.SelectVO;
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
@RequestMapping("/api/system/role/")
public class RoleController {

    private final RoleFacade roleFacade;


    @GetMapping("/getSelectableRoleList")
    public CommonVo<List<SelectVO>> getSelectableRoleList() {
        return CommonVo.success(roleFacade.getSelectableRoleList());
    }

    @PostMapping("/getPageableRoleList")
    public CommonVo<Object> getPageableRoleList(@RequestBody RoleForm.RoleSearchForm form) {
        return CommonVo.success(roleFacade.getPageableRoleList(form));
    }

    @PostMapping("/updateRoleMenuRelation")
    public CommonVo<Object> updateRoleMenuRelation(@RequestBody RoleForm.UpdateRoleMenuForm form) {
        User user = RequestUtil.getCurrentUser();
        roleFacade.updateRoleMenuRelation(form, user.getAccount());
        return CommonVo.success();
    }

    @PostMapping("/createRoleWithRoleMenuRelation")
    public CommonVo<Object> createRoleWithRoleMenuRelation(@RequestBody @Valid RoleForm.CreateRoleForm form) {
        User user = RequestUtil.getCurrentUser();
        roleFacade.createRoleWithRoleMenuRelation(form, user.getAccount());
        return CommonVo.success();
    }

    @PostMapping("/deleteRole")
    public CommonVo<Object> deleteRole(@RequestBody RoleForm.DeleteRoleForm form) {
        User user = RequestUtil.getCurrentUser();
        roleFacade.deleteRole(form, user.getAccount());
        return CommonVo.success();
    }


}
