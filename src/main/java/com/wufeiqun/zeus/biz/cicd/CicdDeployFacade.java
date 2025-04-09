package com.wufeiqun.zeus.biz.cicd;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wufeiqun.zeus.biz.celery.CeleryTaskExecutor;
import com.wufeiqun.zeus.biz.celery.CeleryTaskParamBuilder;
import com.wufeiqun.zeus.biz.celery.entity.CeleryCicdDeployForm;
import com.wufeiqun.zeus.biz.cicd.entity.CicdDeployContext;
import com.wufeiqun.zeus.biz.cicd.entity.CicdDeployForm;
import com.wufeiqun.zeus.biz.cicd.entity.CicdDeployRecordVO;
import com.wufeiqun.zeus.biz.cicd.enums.*;
import com.wufeiqun.zeus.biz.cmdb.enums.EnvironmentEnum;
import com.wufeiqun.zeus.common.entity.SelectVO;
import com.wufeiqun.zeus.common.utils.AsyncUtil;
import com.wufeiqun.zeus.common.utils.sender.WorkWechatSender;
import com.wufeiqun.zeus.dao.ApplicationDeployConfig;
import com.wufeiqun.zeus.dao.CicdBuildRecord;
import com.wufeiqun.zeus.dao.CicdDeployRecord;
import com.wufeiqun.zeus.service.*;
import com.wufeiqun.zeus.service.impl.GitlabService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author wufeiqun
 * @date 2022-08-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CicdDeployFacade {
    private final CicdDeployPreProcessor cicdDeployPreProcessor;
    private final CicdDeployAdapter cicdDeployAdapter;
    private final ICicdBuildRecordService cicdBuildRecordService;
    private final ICicdDeployRecordService cicdDeployRecordService;
    private final CeleryTaskParamBuilder celeryTaskParamBuilder;
    private final CeleryTaskExecutor celeryTaskExecutor;

    private final WorkWechatSender workWechatSender;
    private final IApplicationDeployConfigService applicationDeployConfigService;

    private final IApplicationResourceRelationService applicationResourceRelationService;
    private final GitlabService gitlabService;
    private final AsyncUtil asyncUtil;

    private final DeploymentYamlFactory deploymentYamlFactory;
    private final ServiceYamlFactory serviceYamlFactory;
    private final IUserService userService;
    private final StringRedisTemplate redisTemplate;
    private final WebClient webClient;


    /**
     * 回滚和发布走的是同一个流程, 只是发布的不同的包而已
     */
    public CicdDeployRecord runDeploy(CicdDeployForm.RunDeployForm form, String operator){
        // 构建上下文, 所有流程中用到的数据都在这里预处理计算并放到统一的变量中
        CicdDeployContext deployContext = cicdDeployPreProcessor.prepareDeployContext(form, operator);
        log.info("用户 [{}] 执行发布/回滚, 参数: {}, 转换后的上下文: {}", operator, form, deployContext);
        cicdDeployPreProcessor.validate(deployContext, operator);
        // 创建发布/回滚记录
        CicdDeployRecord deployRecord = cicdDeployAdapter.convertToCicdDeployRecord(deployContext);
        cicdDeployRecordService.save(deployRecord);
        deployContext.setDeployRecordId(deployRecord.getId());
        // 判断发布类型, 虚机还是容器
        if (deployContext.getRunDeployForm().getDockerDeploy()){
            deploymentYamlFactory.prepare(deployContext);
            deploymentYamlFactory.create();
            serviceYamlFactory.prepare(deployContext);
            serviceYamlFactory.create();
        }
        // 调用异步任务执行发布/回滚
        CeleryCicdDeployForm celeryCicdDeployForm = celeryTaskParamBuilder.createCicdDeployForm(deployContext);
        boolean result;

        if (form.getIsRollback()){
            result = celeryTaskExecutor.doRollback(celeryCicdDeployForm);
        } else {
            result = celeryTaskExecutor.doDeploy(celeryCicdDeployForm);
        }

        if (!result) {
            onExecDeployError(deployContext);
        }

        return deployRecord;
    }

    public CicdDeployRecordVO getLatestDeployRecord(CicdDeployForm.DeployRecordQueryForm form){

        List<CicdDeployRecord> list = cicdDeployRecordService.list(buildQueryWrapper(form));
        if (CollectionUtil.isEmpty(list)){
            return new CicdDeployRecordVO();
        } else {
            return cicdDeployAdapter.transformToDeployRecord(list.getFirst());
        }

    }

    public IPage<CicdDeployRecordVO> getPageableDeployRecordList(CicdDeployForm.DeployRecordQueryForm form){

        IPage<CicdDeployRecord> pageRequest = new Page<>(form.getPageNum(), form.getPageSize());

        IPage<CicdDeployRecord> cicdDeployRecordPage = cicdDeployRecordService.page(pageRequest, buildQueryWrapper(form));
        IPage<CicdDeployRecordVO> cicdDeployRecordVOPage = new Page<>(cicdDeployRecordPage.getCurrent(), cicdDeployRecordPage.getSize(), cicdDeployRecordPage.getTotal());
        cicdDeployRecordVOPage.setRecords(cicdDeployAdapter.transformToDeployRecordList(cicdDeployRecordPage.getRecords()));
        return cicdDeployRecordVOPage;
    }

    private QueryWrapper<CicdDeployRecord> buildQueryWrapper(CicdDeployForm.DeployRecordQueryForm form){
        QueryWrapper<CicdDeployRecord> queryWrapper = new QueryWrapper<>();

        queryWrapper.orderByDesc("id");
        queryWrapper.eq(Objects.nonNull(form.getBuildRecordId()), "build_record_id", form.getBuildRecordId());
        queryWrapper.eq(StringUtils.isNotBlank(form.getAppCode()), "app_code", form.getAppCode());
        queryWrapper.eq(StringUtils.isNotBlank(form.getEnvCode()), "env_code", form.getEnvCode());
        queryWrapper.eq(BooleanUtils.isTrue(form.getDockerDeploy()), "docker_deploy", form.getDockerDeploy());
        queryWrapper.eq(BooleanUtils.isTrue(form.getIsRollback()), "is_rollback", form.getIsRollback());
        queryWrapper.eq(StringUtils.isNotBlank(form.getCreateUser()), "create_user", form.getCreateUser());

        return queryWrapper;
    }

    private void onExecDeployError(CicdDeployContext context){
        log.error("onExecBuildError, 任务构建失败, params={}", context);
        CicdDeployRecord record = new CicdDeployRecord();
        record.setDeployStatus(CicdDeployStatusEnum.FAILED.getCode());
        record.setId(context.getDeployRecordId());
        cicdDeployRecordService.updateById(record);
    }

    public void updateDeployRecordStatus(CicdDeployForm.CallBackUpdateDeployRecordForm form){
        log.info("updateDeployRecordStatus, params={}", form);
        CicdDeployRecord record = new CicdDeployRecord();
        record.setDeployStatus(form.getStatus());
        record.setId(form.getDeployRecordId());
        cicdDeployRecordService.updateById(record);
        record = cicdDeployRecordService.getById(form.getDeployRecordId());
        CicdBuildRecord buildRecord = cicdBuildRecordService.getById(record.getBuildRecordId());
        List<ApplicationDeployConfig> configList = applicationDeployConfigService.getByAppcode(record.getAppCode());
        String workWeixinToken = configList.getFirst().getWorkWeixinToken();
        if (StringUtils.isEmpty(workWeixinToken)){
            log.warn("企业微信配置为空, 发布通知未发送: {}", JSON.toJSONString(record));
            return;
        }

        if (record.getRollback()){
            workWechatSender.send(workWeixinToken,
                    NoticeTemplateMaker.getRollbackMessage(record, userService.getAccountUserMap()));
        } else {
            workWechatSender.send(workWeixinToken,
                    NoticeTemplateMaker.getDeployMessage(record, buildRecord, userService.getAccountUserMap()));
        }

        // 部署成功执行任务, 回滚的跳过
        if (!record.getRollback() && CicdDeployStatusEnum.SUCCESS.getCode().equals(form.getStatus())){
            onDeploySuccess(record, configList);
        }

    }

    private void onDeploySuccess(CicdDeployRecord record, List<ApplicationDeployConfig> configList){
        asyncUtil.doTask(()-> asyncWork(record));
    }

    /**
     *
     * 异步任务
     */
    private void asyncWork(CicdDeployRecord record){
        //TODO 这里需要优化, 暂时先放在这里了
    }

    public Map<String, Object> getSelectableNamespaceList(){
        Map<String, Object> map = new HashMap<>();

        List<SelectVO> testList = new ArrayList<>();
        List<SelectVO> preList = new ArrayList<>();
        List<SelectVO> prodList = new ArrayList<>();
        for (TestNamespaceEnum v: TestNamespaceEnum.values()) {
            SelectVO select = new SelectVO();
            select.setValue(v.getNamespace());
            select.setLabel(String.format("%s(%s)", v.getDesc(), v.getNamespace()));
            testList.add(select);
        }
        for (PreNamespaceEnum v: PreNamespaceEnum.values()) {
            SelectVO select = new SelectVO();
            select.setValue(v.getNamespace());
            select.setLabel(String.format("%s(%s)", v.getDesc(), v.getNamespace()));
            preList.add(select);
        }
        for (ProdNamespaceEnum v: ProdNamespaceEnum.values()) {
            SelectVO select = new SelectVO();
            select.setValue(v.getNamespace());
            select.setLabel(String.format("%s(%s)", v.getDesc(), v.getNamespace()));
            prodList.add(select);
        }
        map.put(EnvironmentEnum.TEST.getCode(), testList);
        map.put(EnvironmentEnum.PRE.getCode(), preList);
        map.put(EnvironmentEnum.PROD.getCode(), prodList);
        return map;
    }

    public List<SelectVO> getSelectableKubernetesScheduleStrategyList(){
        List<SelectVO> selectVOList = new ArrayList<>();

        for (ScheduleStrategyEnum item : ScheduleStrategyEnum.values()){
            SelectVO vo = new SelectVO(item.getName(), item.getCode());
            selectVOList.add(vo);
        }

        return selectVOList;
    }

}
