package com.wufeiqun.zeus.biz.cicd;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wufeiqun.zeus.biz.celery.CeleryTaskExecutor;
import com.wufeiqun.zeus.biz.celery.CeleryTaskParamBuilder;
import com.wufeiqun.zeus.biz.celery.entity.CeleryCicdBuildForm;
import com.wufeiqun.zeus.biz.cicd.entity.CicdBuildContext;
import com.wufeiqun.zeus.biz.cicd.entity.CicdBuildForm;
import com.wufeiqun.zeus.biz.cicd.entity.CicdBuildRecordVO;
import com.wufeiqun.zeus.biz.cicd.enums.ArtifactTypeEnum;
import com.wufeiqun.zeus.biz.cicd.enums.CicdBuildStatusEnum;
import com.wufeiqun.zeus.biz.cicd.enums.RuntimeVersionEnum;
import com.wufeiqun.zeus.biz.cmdb.enums.EnvironmentEnum;
import com.wufeiqun.zeus.common.entity.SelectVO;
import com.wufeiqun.zeus.common.exception.ServiceException;
import com.wufeiqun.zeus.common.utils.sender.WorkWechatSender;
import com.wufeiqun.zeus.dao.ApplicationDeployConfig;
import com.wufeiqun.zeus.dao.CicdBuildRecord;
import com.wufeiqun.zeus.dao.User;
import com.wufeiqun.zeus.service.IApplicationDeployConfigService;
import com.wufeiqun.zeus.service.ICicdBuildRecordService;
import com.wufeiqun.zeus.service.IUserService;
import com.wufeiqun.zeus.service.impl.GitlabService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;
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
    private final ICicdBuildRecordService cicdBuildRecordService;
    private final CeleryTaskParamBuilder celeryTaskParamBuilder;
    private final CeleryTaskExecutor celeryTaskExecutor;
    private final WorkWechatSender workWechatSender;
    private final GitlabService gitlabService;
    private final IApplicationDeployConfigService applicationDeployConfigService;
    private final IUserService userService;


    public CicdBuildRecordVO runBuild(CicdBuildForm.RunBuildForm form, String operator){
        log.info("用户 [{}] 执行构建, 参数: {}", operator, form);
        // 构建上下文, 所有流程中用到的数据都在这里预处理计算并放到统一的变量中
        CicdBuildContext buildContext = cicdBuildPreProcessor.prepareBuildContext(form, operator);
        cicdBuildPreProcessor.validate(buildContext, operator);
        // 创建构建记录
        CicdBuildRecord buildRecord = cicdBuildAdapter.convertToCicdBuildRecord(buildContext);
        cicdBuildRecordService.save(buildRecord);
        buildContext.setBuildRecordId(buildRecord.getId());
        // 调用异步任务执行构建
        CeleryCicdBuildForm celeryCicdBuildForm = celeryTaskParamBuilder.createCicdBuildForm(buildContext);
        boolean result = celeryTaskExecutor.doBuild(celeryCicdBuildForm);
        if (!result) {
            onExecBuildError(buildContext);
        }
        return convertToVO(Collections.singletonList(buildRecord)).getFirst();
    }

    public CicdBuildRecordVO getBuildRecordById(Long buildRecordId){
        CicdBuildRecord record = cicdBuildRecordService.getById(buildRecordId);
        return convertToVO(Collections.singletonList(record)).getFirst();
    }

    private List<CicdBuildRecordVO> convertToVO(List<CicdBuildRecord> list){
        Map<String, User> userMap =  userService.getAccountUserMap();
        return list.stream().map(item -> {
            CicdBuildRecordVO vo = new CicdBuildRecordVO();
            BeanUtil.copyProperties(item, vo);
            vo.setStatusDesc(CicdBuildStatusEnum.descWithCode(item.getBuildStatus()));
            vo.setBuildEnvCode(EnvironmentEnum.descWithCode(item.getBuildEnvCode()));
            if (userMap.containsKey(item.getCreateUser())){
                vo.setCreateUser(userMap.get(item.getCreateUser()).getUsername());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    public List<SelectVO> getSelectableArtifactTypeList(){
        List<SelectVO> list = new ArrayList<>();
        for (ArtifactTypeEnum value : ArtifactTypeEnum.values()) {
            SelectVO select = new SelectVO();
            select.setValue(value.getType());
            select.setLabel(MessageFormat.format("{0}-{1}", value.getType(), value.getDesc()));
            list.add(select);
        }
        return list;
    }

    public CicdBuildRecordVO getLatestBuildRecord(String appCode, String envCode){
        QueryWrapper<CicdBuildRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.eq("build_app_code", appCode).eq("build_env_code", envCode);
        CicdBuildRecord latestOne = cicdBuildRecordService.getOne(queryWrapper);

        if (Objects.isNull(latestOne)){
            return new CicdBuildRecordVO();
        } else {
            return convertToVO(Collections.singletonList(latestOne)).getFirst();
        }
    }

    public IPage<CicdBuildRecordVO> getPageableBuildRecordList(CicdBuildForm.BuildRecordSearchForm form){
        Page<CicdBuildRecord> pageRequest = new Page<>(form.getPageNum(), form.getPageSize());

        QueryWrapper<CicdBuildRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.eq(StringUtils.isNotBlank(form.getBuildAppCode()), "build_app_code", form.getBuildAppCode());
        queryWrapper.eq(StringUtils.isNotBlank(form.getBuildEnvCode()), "build_env_code", form.getBuildEnvCode());
        queryWrapper.eq(StringUtils.isNotBlank(form.getCreateUser()), "create_user", form.getCreateUser());
        queryWrapper.eq(Objects.nonNull(form.getStatus()), "build_status", form.getStatus());

        IPage<CicdBuildRecord> cicdBuildRecordPage = cicdBuildRecordService.page(pageRequest, queryWrapper);

        IPage<CicdBuildRecordVO> voPage = new Page<>(cicdBuildRecordPage.getCurrent(), cicdBuildRecordPage.getSize(), cicdBuildRecordPage.getTotal());
        voPage.setRecords(convertToVO(cicdBuildRecordPage.getRecords()));
        return voPage;
    }

    /**
     * 获取用于回滚的构建记录列表, 最近成功的3次生产构建
     */
    public List<SelectVO> getRollbackBuildRecordList(CicdBuildForm.BuildRecordSearchForm form){
        IPage<CicdBuildRecord> pageRequest = new Page<>(1, 3);

        QueryWrapper<CicdBuildRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.eq("build_app_code", form.getBuildAppCode())
                .eq("build_env_code", EnvironmentEnum.PROD.getCode())
                .eq("build_status", CicdBuildStatusEnum.SUCCESS.getCode());

        IPage<CicdBuildRecord> cicdBuildRecordPage = cicdBuildRecordService.page(pageRequest, queryWrapper);
        return cicdBuildRecordPage.getRecords().stream().map((item)->{
            SelectVO vo = new SelectVO();
            vo.setLabel(item.getBuildTag());
            vo.setValue(item.getId());
            return vo;}).collect(Collectors.toList());

    }

    private void onExecBuildError(CicdBuildContext context){
        log.error("onExecBuildError, 任务构建失败, params={}", context);
        CicdBuildRecord record = new CicdBuildRecord();
        record.setBuildStatus(CicdBuildStatusEnum.FAILED.getCode());
        record.setId(context.getBuildRecordId());
        cicdBuildRecordService.updateById(record);
    }

    public void updateBuildRecordStatus(CicdBuildForm.UpdateBuildRecordStatusForm form){
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
        cicdBuildRecordService.updateById(record);
        // 构建结果通知
        buildNotice(form.getBuildRecordId());
    }

    public void buildNotice(Long buildRecordId){
        CicdBuildRecord record = cicdBuildRecordService.getById(buildRecordId);
        List<ApplicationDeployConfig> configList = applicationDeployConfigService.getByAppcode(record.getBuildAppCode());
        String workWeixinToken = configList.getFirst().getWorkWeixinToken();
        if (StringUtils.isEmpty(workWeixinToken)){
            log.warn("企业微信配置为空, 构建通知未发送: {}", JSON.toJSONString(record));
            return;
        }

        workWechatSender.send(workWeixinToken,
                NoticeTemplateMaker.getBuildMessage(record, gitlabService.resolveGitAddrAttribute(configList.get(0).getGit()),
                        userService.getAccountUserMap()));
    }

    public List<SelectVO> getBranches(String appCode){
        List<ApplicationDeployConfig> configList = applicationDeployConfigService.getByAppcode(appCode);
        if(CollectionUtil.isEmpty(configList)){
            throw new ServiceException("发布配置不完整: " + appCode);
        }

        String gitAddress = configList.getFirst().getGit();

        return gitlabService.getBranches(gitAddress);
    }

    public List<SelectVO> getSelectableRuntimeVersionList(String pkgType){

        if (ArtifactTypeEnum.JAR.getType().equals(pkgType) || ArtifactTypeEnum.JARAPI.getType().equals(pkgType)){
            return RuntimeVersionEnum.getJDKList().stream().map((item) -> new SelectVO(item.name(), item.getVersion())).collect(Collectors.toList());
        }

        if (ArtifactTypeEnum.STATICFE.getType().equals(pkgType) || ArtifactTypeEnum.NODE.getType().equals(pkgType)){
            return RuntimeVersionEnum.getNODEJSList().stream().map((item) -> new SelectVO(item.name(), item.getVersion())).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
