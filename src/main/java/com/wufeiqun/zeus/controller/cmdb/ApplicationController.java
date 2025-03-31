package com.wufeiqun.zeus.controller.cmdb;

import com.wufeiqun.zeus.biz.cmdb.ApplicationFacade;
import com.wufeiqun.zeus.biz.cmdb.entity.ApplicationForm;
import com.wufeiqun.zeus.common.entity.CommonVo;
import com.wufeiqun.zeus.common.utils.RequestUtil;
import com.wufeiqun.zeus.dao.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


/**
 * @author wufeiqun
 * @date 2022-07-07
 */
@Slf4j
@RestController
@RequestMapping("/api/cmdb/application")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationFacade applicationFacade;


    @PostMapping("/create")
    public CommonVo<Integer> createApplication(@RequestBody @Valid ApplicationForm.ApplicationAddForm form){
        User user = RequestUtil.getCurrentUser();
        applicationFacade.createApplication(form, user.getAccount());
        return CommonVo.success();
    }

    @PostMapping("/update")
    public CommonVo<Object> updateApplication(@RequestBody @Valid ApplicationForm.ApplicationUpdateForm form){
        User user = RequestUtil.getCurrentUser();
        applicationFacade.updateApplication(form, user.getAccount());
        return CommonVo.success();
    }

    @PostMapping("/getPageableApplicationList")
    public CommonVo<Object> getPageableApplicationList(@RequestBody @Valid ApplicationForm.ApplicationQueryForm form){
        User user = RequestUtil.getCurrentUser();
        return CommonVo.success(applicationFacade.getPageableApplicationList(form, user.getAccount()));
    }

    @PostMapping("/isApplicationExist")
    public CommonVo<Boolean> isApplicationExist(@RequestBody @Valid ApplicationForm.ApplicationExistForm form){
        return CommonVo.success(applicationFacade.isApplicationExist(form));
    }

    @PostMapping("/delete")
    public CommonVo<Object> delete(@RequestBody @Valid ApplicationForm.ApplicationDeleteForm form){
        User user = RequestUtil.getCurrentUser();
        applicationFacade.deleteApplication(form.getId(), user.getAccount());
        return CommonVo.success();
    }

    @PostMapping("/getApplicationDetailByCode")
    public CommonVo<Object> getApplicationDetailByCode(@RequestBody @Valid ApplicationForm.ApplicationQueryForm form){
        return CommonVo.success(applicationFacade.getApplicationDetailByCode(form));
    }

    @PostMapping("/favoriteApplication/{appCode}")
    public CommonVo<Object> favoriteApplication(@PathVariable String appCode){
        User user = RequestUtil.getCurrentUser();
        applicationFacade.processMyFavoriteApplication(appCode, user.getAccount(), true);
        return CommonVo.success();
    }
    @PostMapping("/cancelFavoriteApplication/{appCode}")
    public CommonVo<Object> cancelFavoriteApplication(@PathVariable String appCode){
        User user = RequestUtil.getCurrentUser();
        applicationFacade.processMyFavoriteApplication(appCode, user.getAccount(), false);
        return CommonVo.success();
    }
}
