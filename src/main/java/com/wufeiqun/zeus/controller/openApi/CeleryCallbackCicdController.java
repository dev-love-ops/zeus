package com.wufeiqun.zeus.controller.openApi;


import com.wufeiqun.zeus.biz.cicd.CicdBuildFacade;
import com.wufeiqun.zeus.biz.cicd.CicdDeployFacade;
import com.wufeiqun.zeus.biz.cicd.entity.CicdBuildForm;
import com.wufeiqun.zeus.biz.cicd.entity.CicdDeployForm;
import com.wufeiqun.zeus.common.entity.CommonVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@Slf4j
@RestController
@RequestMapping("/openApi/celery/callback/cicd")
@RequiredArgsConstructor
public class CeleryCallbackCicdController {
    private final CicdBuildFacade buildFacade;
    private final CicdDeployFacade deployFacade;



    /**
     * 更新构建记录状态, 同时也会把构建制品的URL/镜像地址等更新到构建记录中
     */
    @PostMapping("/build/updateBuildRecordStatus")
    public CommonVo<Object> updateBuildRecordStatus(@RequestBody @Valid CicdBuildForm.UpdateBuildRecordStatusForm form) {
        buildFacade.updateBuildRecordStatus(form);
        return CommonVo.success();
    }

    /**
     * 更新发布记录状态
     */
    @PostMapping("/deploy/updateDeployRecordStatus")
    public CommonVo<Object> updateDeployRecordStatus(@RequestBody @Valid CicdDeployForm.CallBackUpdateDeployRecordForm form) {
        deployFacade.updateDeployRecordStatus(form);
        return CommonVo.success();
    }

}
