package com.wufeiqun.zeus.controller.cmdb;

import com.wufeiqun.zeus.biz.cmdb.ApplicationFacade;
import com.wufeiqun.zeus.biz.cmdb.entity.ApplicationForm;
import com.wufeiqun.zeus.common.entity.CommonVo;
import com.wufeiqun.zeus.common.utils.RequestUtil;
import com.wufeiqun.zeus.dao.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author wufeiqun
 * @date 2022-07-07
 */
@Slf4j
@RestController
@RequestMapping("/api/cmdb/application/resource/relation")
@RequiredArgsConstructor
public class ApplicationResourceRelationController {
    private final ApplicationFacade applicationFacade;


    @PostMapping("/create")
    public CommonVo<Object> createApplicationResourceRelation(@RequestBody @Valid ApplicationForm.ApplicationResourceCreateOrDeleteForm form){
        User user = RequestUtil.getCurrentUser();
        applicationFacade.createApplicationResourceRelation(form, user.getAccount());
        return CommonVo.success();
    }

    @PostMapping("/delete")
    public CommonVo<Object> deleteApplicationResourceRelation(@RequestBody @Valid ApplicationForm.ApplicationResourceCreateOrDeleteForm form){
        User user = RequestUtil.getCurrentUser();
        applicationFacade.deleteApplicationResourceRelation(form, user.getAccount());
        return CommonVo.success();
    }

    @PostMapping("/getSelectableApplicationResourceList")
    public CommonVo<Object> getSelectableApplicationResourceList(@RequestBody @Valid ApplicationForm.ApplicationResourceQueryForm form){
        return CommonVo.success(applicationFacade.getSelectableApplicationResourceList(form));
    }

    @PostMapping("/getPageableApplicationResourceList")
    public CommonVo<Object> getPageableApplicationResourceList(@RequestBody @Valid ApplicationForm.ApplicationResourceQueryForm form){
        return CommonVo.success(applicationFacade.getPageableApplicationResourceList(form));
    }

    @PostMapping("/getApplicationPodList")
    public CommonVo<Object> getApplicationPodList(@RequestBody @Valid ApplicationForm.ApplicationResourceQueryForm form){
        return CommonVo.success();
    }

}
