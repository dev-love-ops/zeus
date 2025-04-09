package com.wufeiqun.zeus.controller.cicd;


import com.wufeiqun.zeus.biz.celery.CeleryLogExecutor;
import com.wufeiqun.zeus.biz.celery.entity.ReadLogForm;
import com.wufeiqun.zeus.biz.cicd.CicdDeployFacade;
import com.wufeiqun.zeus.biz.cicd.entity.CicdDeployForm;
import com.wufeiqun.zeus.biz.cicd.entity.CicdDeployRecordVO;
import com.wufeiqun.zeus.common.entity.CommonVo;
import com.wufeiqun.zeus.common.utils.RequestUtil;
import com.wufeiqun.zeus.dao.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/cicd/deploy/")
@RequiredArgsConstructor
public class CicdDeployController {
    private final CicdDeployFacade deployFacade;
    private final CeleryLogExecutor celeryLogExecutor;


    /**
     * 运行构建
     */
    @PostMapping("/run")
    public CommonVo<Object> runDeploy(@RequestBody @Valid CicdDeployForm.RunDeployForm form) {
        User user = RequestUtil.getCurrentUser();
        form.setIsRollback(false);
        return CommonVo.success(deployFacade.runDeploy(form, user.getAccount()));
    }

    /**
     * 读取发布日志
     */
    @PostMapping("/readLog")
    public CommonVo<Object> readDeployLog(@RequestBody @Valid ReadLogForm form) {
        return CommonVo.success(celeryLogExecutor.readDeployLog(form));
    }

    /**
     * 获取最近一次的发布记录
     */
    @PostMapping("/getLatestDeployRecord")
    public CommonVo<CicdDeployRecordVO> getLatestDeployRecord(@RequestBody @Valid CicdDeployForm.DeployRecordQueryForm form) {
        return CommonVo.success(deployFacade.getLatestDeployRecord(form));
    }

    /**
     * 获取发布记录
     */
    @PostMapping("/getPageableDeployRecordList")
    public CommonVo<Object> getPageableDeployRecordList(@RequestBody @Valid CicdDeployForm.DeployRecordQueryForm form) {
        return CommonVo.success(deployFacade.getPageableDeployRecordList(form));
    }


    /**
     * 容器命名空间下拉框选择
     */
    @GetMapping("/getSelectableNamespaceList")
    public CommonVo<Object> getSelectableNamespaceList() {
        return CommonVo.success(deployFacade.getSelectableNamespaceList());
    }

    /**
     * 容器发布策略下拉框选择
     */
    @GetMapping("/getSelectableKubernetesScheduleStrategyList")
    public CommonVo<Object> getSelectableKubernetesScheduleStrategyList() {
        return CommonVo.success(deployFacade.getSelectableKubernetesScheduleStrategyList());
    }
}
