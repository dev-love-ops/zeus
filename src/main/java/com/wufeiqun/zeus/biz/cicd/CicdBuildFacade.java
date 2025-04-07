package com.wufeiqun.zeus.biz.cicd;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.wufeiqun.zeus.common.utils.sender.WorkWechatSender;
import com.wufeiqun.zeus.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author wufeiqun
 * @date 2022-08-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CicdBuildFacade {
    private final CicdBuildPreProcessor cicdBuildPreProcessor;
    private final CicdBuildAdapter cicdBuildAdapter;
    private final CicdBuildService cicdBuildService;
    private final CeleryTaskParamBuilder celeryTaskParamBuilder;
    private final CeleryTaskExecutor celeryTaskExecutor;
    private final WorkWechatSender workWechatSender;
    private final GitlabService gitlabService;
    private final ApplicationDeployConfigService applicationDeployConfigService;
    private final IUserService userService;


    public CicdBuildVOs.CicdBuildRecordVO runBuild(CicdBuildForms.RunBuildForm form, String operator){
        log.info("用户 [{}] 执行构建, 参数: {}", operator, form);
        // 构建上下文, 所有流程中用到的数据都在这里预处理计算并放到统一的变量中
        CicdBuildContext buildContext = cicdBuildPreProcessor.prepareBuildContext(form, operator);
        cicdBuildPreProcessor.validate(buildContext, operator);
        // 创建构建记录
        CicdBuildRecord buildRecord = cicdBuildAdapter.convertToCicdBuildRecord(buildContext);
        cicdBuildService.createBuildRecord(buildRecord);
        buildContext.setBuildRecordId(buildRecord.getId());
        // 调用异步任务执行构建
        CeleryCicdBuildForm celeryCicdBuildForm = celeryTaskParamBuilder.createCicdBuildForm(buildContext);
        boolean result = celeryTaskExecutor.doBuild(celeryCicdBuildForm);
        if (!result) {
            onExecBuildError(buildContext);
        }
        return cicdBuildAdapter.transformToBuildRecord(Collections.singletonList(buildRecord)).get(0);
    }

    public CicdBuildVOs.CicdBuildRecordVO getBuildRecordById(Long buildRecordId){
        CicdBuildRecord record = cicdBuildService.getBuildRecordById(buildRecordId);
        return cicdBuildAdapter.transformToBuildRecord(Collections.singletonList(record)).get(0);
    }

    public List<SelectVO> getSelectablePkgTypeList(){
        List<SelectVO> list = new ArrayList<>();
        for (CicdBuildPkgTypeEnum value : CicdBuildPkgTypeEnum.values()) {
            SelectVO select = new SelectVO();
            select.setValue(value.getType());
            select.setLabel(MessageFormat.format("{0}-{1}", value.getType(), value.getDesc()));
            list.add(select);
        }
        return list;
    }

    public CicdBuildVOs.CicdBuildRecordVO getLatestBuildRecord(CicdBuildForms.BuildRecordSearchForm form){
        CicdBuildRecord buildRecord = new CicdBuildRecord();
        BeanUtil.copyProperties(form, buildRecord);
        List<CicdBuildRecord> list = cicdBuildService.getBuildRecordList(buildRecord);
        if (CollectionUtil.isEmpty(list)){
            return new CicdBuildVOs.CicdBuildRecordVO();
        } else {
            return cicdBuildAdapter.transformToBuildRecord(list).get(0);
        }
    }

    public PageInfo getPageableBuildRecordList(CicdBuildForms.BuildRecordSearchForm form){
        PageHelper.startPage(form.getPageNum(), form.getPageSize());
        CicdBuildRecordExample example = new CicdBuildRecordExample();
        cicdBuildAdapter.resolveBuildRecordQuery(example, form);
        List<CicdBuildRecord> list = cicdBuildService.getBuildRecordListByExample(example);
        PageInfo page = new PageInfo<>(list);
        page.setList(cicdBuildAdapter.transformToBuildRecord(list));
        return page;
    }

    /**
     * 用于回滚的时候选择回滚到哪个版本
     */
    public List<SelectVO> getSelectableBuildRecordList(CicdBuildForms.BuildRecordSearchForm form){
        List<CicdBuildRecord> list = getRollbackBuildRecordList(form);
        // 只过滤构建成功的
        return list.stream().filter(item -> CicdBuildStatusEnum.SUCCESS.getCode().equals(item.getBuildStatus())).map((record)->{
            SelectVO vo = new SelectVO();
            vo.setLabel(record.getBuildTag());
            vo.setValue(record.getId());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 获取用于回滚的构建记录列表, 最近成功的5次生产构建
     */
    private List<CicdBuildRecord> getRollbackBuildRecordList(CicdBuildForms.BuildRecordSearchForm form){
        PageHelper.startPage(1, 3);
        CicdBuildRecordExample example = new CicdBuildRecordExample();
        form.setStatus(CicdBuildStatusEnum.SUCCESS.getCode());
        form.setBuildEnvCode(EnvironmentEnum.PROD.getCode());
        cicdBuildAdapter.resolveBuildRecordQuery(example, form);
        return cicdBuildService.getBuildRecordListByExample(example);
    }



    private void onExecBuildError(CicdBuildContext context){
        log.error("onExecBuildError, 任务构建失败, params={}", context);
        CicdBuildRecord record = new CicdBuildRecord();
        record.setBuildStatus(CicdBuildStatusEnum.FAILED.getCode());
        record.setId(context.getBuildRecordId());
        cicdBuildService.updateBuildRecordSelective(record);
    }

    public void updateBuildRecordStatus(CicdBuildForms.UpdateBuildRecordStatusForm form){
        log.info("updateBuildRecordStatus, params={}", form);
        CicdBuildRecord record = new CicdBuildRecord();
        record.setBuildStatus(form.getStatus());
        if (StringUtils.isNotEmpty(form.getPkgUrl())){
            record.setBuildTargetUrl(form.getPkgUrl());
        }
        if (StringUtils.isNotEmpty(form.getImageUrl())){
            record.setBuildImageUrl(form.getImageUrl());;
        }
        record.setDuration(form.getDuration());
        record.setId(form.getBuildRecordId());
        cicdBuildService.updateBuildRecordSelective(record);
        // 构建结果通知
        buildNotice(form.getBuildRecordId());
    }

    public void buildNotice(Long buildRecordId){
        CicdBuildRecord record = cicdBuildService.getBuildRecordById(buildRecordId);
        List<ApplicationDeployConfig> configList = applicationDeployConfigService.getByAppCode(record.getBuildAppCode());
        String workWeixinToken = configList.get(0).getWorkWeixinToken();
        if (StringUtils.isEmpty(workWeixinToken)){
            log.warn("企业微信配置为空, 构建通知未发送: {}", JSON.toJSONString(record));
            return;
        }

        workWechatSender.send(workWeixinToken,
                NoticeTemplateMaker.getBuildMessage(record, gitlabService.resolveGitAddrAttribute(configList.get(0).getGit()),
                        userService.getAccountToUserMap()));
    }

    public List<SelectVO> getBranches(String appCode){
        List<ApplicationDeployConfig> configList = applicationDeployConfigService.getByAppCode(appCode);
        if(CollectionUtil.isEmpty(configList)){
            throw new ServiceException("发布配置不完整: " + appCode);
        }

        String gitAddress = configList.get(0).getGit();

        return gitlabService.getBranches(gitAddress);
    }

    public List<SelectVO> getSelectableRuntimeVersionList(String pkgType){

        if (CicdBuildPkgTypeEnum.JAR.getType().equals(pkgType) || CicdBuildPkgTypeEnum.JARAPI.getType().equals(pkgType)){
            return RuntimeVersionEnum.getJDKList().stream().map((item) -> new SelectVO(item.name(), item.getVersion())).collect(Collectors.toList());
        }

        if (CicdBuildPkgTypeEnum.STATICFE.getType().equals(pkgType) || CicdBuildPkgTypeEnum.NODE.getType().equals(pkgType)){
            return RuntimeVersionEnum.getNODEJSList().stream().map((item) -> new SelectVO(item.name(), item.getVersion())).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
