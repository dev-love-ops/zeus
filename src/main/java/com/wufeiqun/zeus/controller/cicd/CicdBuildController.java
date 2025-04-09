package com.wufeiqun.zeus.controller.cicd;


import com.wufeiqun.zeus.biz.celery.CeleryLogExecutor;
import com.wufeiqun.zeus.biz.celery.entity.ReadLogForm;
import com.wufeiqun.zeus.biz.cicd.CicdBuildFacade;
import com.wufeiqun.zeus.biz.cicd.entity.CicdBuildForm;
import com.wufeiqun.zeus.biz.cicd.entity.CicdBuildRecordVO;
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
@RequestMapping("/api/cicd/build/")
@RequiredArgsConstructor
public class CicdBuildController {
    private final CicdBuildFacade buildFacade;
    private final CeleryLogExecutor celeryLogExecutor;


    /**
     * 运行构建
     */
    @PostMapping("/run")
    public CommonVo<CicdBuildRecordVO> runBuild(@RequestBody @Valid CicdBuildForm.RunBuildForm runBuildForm) {
        User user = RequestUtil.getCurrentUser();
        return CommonVo.success(buildFacade.runBuild(runBuildForm, user.getAccount()));
    }

    /**
     * 读取构建日志
     */
    @PostMapping("/readLog")
    public CommonVo<Object> readBuildLog(@RequestBody @Valid ReadLogForm form) {
        return CommonVo.success(celeryLogExecutor.readBuildLog(form));
    }

    /**
     * 获取最近一次的构建记录
     */
    @PostMapping("/getLatestBuildRecord")
    public CommonVo<CicdBuildRecordVO> getLastRecord(@RequestBody @Valid CicdBuildForm.BuildRecordSearchForm form) {
        return CommonVo.success(buildFacade.getLatestBuildRecord(form.getBuildAppCode(), form.getBuildEnvCode()));
    }

    /**
     * 根据buildRecordId获取详细信息
     */
    @GetMapping("/record/{buildRecordId}")
    public CommonVo<CicdBuildRecordVO> getDetailById(@PathVariable Long buildRecordId) {
        return CommonVo.success(buildFacade.getBuildRecordById(buildRecordId));
    }

    /**
     * 制品类型下拉框选择
     */
    @GetMapping("/getSelectableArtifactTypeList")
    public CommonVo<Object> getSelectableArtifactTypeList() {
        return CommonVo.success(buildFacade.getSelectableArtifactTypeList());
    }

    /**
     * 获取构建记录列表
     */
    @PostMapping("/getPageableBuildRecordList")
    public CommonVo<Object> getPageableBuildRecordList(@RequestBody @Valid CicdBuildForm.BuildRecordSearchForm form) {
        return CommonVo.success(buildFacade.getPageableBuildRecordList(form));
    }

    /**
     * 获取构建记录下拉框列表, 用于回滚选择版本号
     */
    @PostMapping("/getSelectableBuildRecordList")
    public CommonVo<Object> getSelectableBuildRecordList(@RequestBody @Valid CicdBuildForm.BuildRecordSearchForm form) {
        return CommonVo.success(buildFacade.getRollbackBuildRecordList(form));
    }

    /**
     * 根据appCode获取代码分支列表
     */
    @GetMapping("/getBranchesByAppCode/{appCode}")
    public CommonVo<List<SelectVO>> getBranchesByAppCode(@PathVariable String appCode) {
        return CommonVo.success(buildFacade.getBranches(appCode));
    }

    /**
     * 根据应用的pkgType获取RuntimeVersion列表
     */
    @GetMapping("/getSelectableRuntimeVersionList/{pkgType}")
    public CommonVo<List<SelectVO>> getSelectableRuntimeVersionList(@PathVariable String pkgType) {
        return CommonVo.success(buildFacade.getSelectableRuntimeVersionList(pkgType));
    }
}
