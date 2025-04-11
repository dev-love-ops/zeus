package com.wufeiqun.zeus.controller.cicd;


import com.wufeiqun.zeus.biz.celery.CeleryLogExecutor;
import com.wufeiqun.zeus.biz.celery.entity.ReadLogForm;
import com.wufeiqun.zeus.biz.cicd.CicdDeployFacade;
import com.wufeiqun.zeus.biz.cicd.entity.CicdDeployForm;
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



@Slf4j
@RestController
@RequestMapping("/api/cicd/rollback/")
@RequiredArgsConstructor
public class CicdRollbackController {
    private final CicdDeployFacade deployFacade;
    private final CeleryLogExecutor celeryLogExecutor;


    /**
     * 运行回滚
     */
    @PostMapping("/run")
    public CommonVo<Object> runRollback(@RequestBody @Valid CicdDeployForm.RunDeployForm form) {
        form.setIsRollback(true);
        User user = RequestUtil.getCurrentUser();
        return CommonVo.success(deployFacade.runDeploy(form, user.getAccount()));
    }

    /**
     * 读取回滚日志
     */
    @PostMapping("/readLog")
    public CommonVo<Object> readRollbackLog(@RequestBody @Valid ReadLogForm form) {
        return CommonVo.success(celeryLogExecutor.readRollbackLog(form));
    }

    /**
     * 获取回滚记录
     */
    @PostMapping("/getPageableRollbackRecordList")
    public CommonVo<Object> getPageableRollbackRecordList(@RequestBody @Valid CicdDeployForm.DeployRecordQueryForm form) {
        form.setIsRollback(true);
        return CommonVo.success(deployFacade.getPageableDeployRecordList(form));
    }
}
