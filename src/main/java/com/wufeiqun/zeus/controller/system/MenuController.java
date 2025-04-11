package com.wufeiqun.zeus.controller.system;

import com.wufeiqun.zeus.biz.system.MenuFacade;
import com.wufeiqun.zeus.biz.system.entity.MenuForm;
import com.wufeiqun.zeus.common.entity.CommonVo;
import com.wufeiqun.zeus.common.utils.RequestUtil;
import com.wufeiqun.zeus.dao.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/system/menu/")
@RequiredArgsConstructor
public class MenuController {

    private final MenuFacade menuFacade;



    @GetMapping("/getMenuTreeList")
    public CommonVo<Object> getMenuTreeList() {
        return CommonVo.success(menuFacade.getMenuTreeList());
    }

    @PostMapping("/createMenu")
    public CommonVo<Object> createMenu(@RequestBody MenuForm.UpdateMenuForm form) {
        User user = RequestUtil.getCurrentUser();
        menuFacade.createMenu(form, user.getAccount());
        return CommonVo.success();
    }

    @PostMapping("/updateMenu")
    public CommonVo<Object> updateMenu(@RequestBody MenuForm.UpdateMenuForm form) {
        User user = RequestUtil.getCurrentUser();
        menuFacade.updateMenu(form, user.getAccount());
        return CommonVo.success();
    }

    @PostMapping("/deleteMenu")
    public CommonVo<Object> deleteMenu(@RequestBody MenuForm.UpdateMenuForm form) {
        User user = RequestUtil.getCurrentUser();
        menuFacade.deleteMenu(form, user.getAccount());
        return CommonVo.success();
    }
}
