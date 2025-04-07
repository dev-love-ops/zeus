package com.wufeiqun.zeus.biz.cmdb;


import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wufeiqun.zeus.biz.cmdb.entity.*;
import com.wufeiqun.zeus.biz.cmdb.enums.EnvironmentEnum;
import com.wufeiqun.zeus.biz.cmdb.enums.ResourceTypeEnum;
import com.wufeiqun.zeus.biz.system.enums.OperationTypeEnum;
import com.wufeiqun.zeus.common.entity.SelectVO;
import com.wufeiqun.zeus.common.exception.ServiceException;
import com.wufeiqun.zeus.common.utils.AsyncUtil;
import com.wufeiqun.zeus.common.utils.PermissionUtil;
import com.wufeiqun.zeus.common.utils.sender.WorkWechatSender;
import com.wufeiqun.zeus.dao.*;
import com.wufeiqun.zeus.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.wufeiqun.zeus.common.constant.GlobalConstant.ZEUS_WORK_WECHAT_ROBOT_KEY;


/**
 * @author wufeiqun
 * @date 2022-07-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationFacade {
    private final IApplicationService applicationService;
    private final ApplicationAdapter applicationAdapter;
    private final IApplicationDeployConfigService applicationDeployConfigService;
    private final IApplicationResourceRelationService applicationResourceRelationService;
    private final PermissionUtil permissionUtil;
    private final AsyncUtil asyncUtil;
    private final IOperationRecordService operationRecordService;
    private final IUserFavoriteApplicationService userFavoriteApplicationService;
    private final WorkWechatSender workWechatSender;
    private final IUserService userService;
    private final IDepartmentService departmentService;
    private final IServerService serverService;


    // -----------------------应用相关的操作-------------------------------
    @Transactional(rollbackFor = Exception.class)
    public void createApplication(ApplicationForm.ApplicationAddForm form, String operator){
        // 权限校验
        if (permissionUtil.noPermission(operator, "cmdb:application:edit")){
            throw new ServiceException("用户无权限: cmdb:application:edit");
        }
        // appCode规则校验
        applicationAdapter.appCodeValidation(form.getCode());
        Application application = new Application();
        BeanUtils.copyProperties(form, application);
        // 生成秘钥
        application.setToken(RandomUtil.randomString(12));
        // 创建发布配置
        createApplicationDeployConfig(form.getCode());

        applicationService.save(application);
    }

    public void updateApplication(ApplicationForm.ApplicationUpdateForm form, String operator){
        if (permissionUtil.noPermission(operator, "cmdb:application:edit")){
            throw new ServiceException("用户无权限: cmdb:application:edit");
        }
        Application application = new Application();
        BeanUtils.copyProperties(form, application);
        // appCode不允许修改
        application.setCode(null);
        application.setUpdateUser(operator);
        applicationService.updateById(application);
    }

    public void deleteApplication(Long id, String operator){
        if (permissionUtil.noPermission(operator, "cmdb:application:edit")){
            throw new ServiceException("用户无权限: cmdb:application:edit");
        }
        Application application = new Application();
        application.setId(id);
        application.setUpdateUser(operator);
        application.setStatus(0);

        applicationService.updateById(application);
    }

    public IPage<ApplicationVO> getPageableApplicationList(ApplicationForm.ApplicationQueryForm form, String operator){
        // 分页请求
        Page<Application> pageRequest = new Page<>(form.getPageNum(), form.getPageSize());
        // 查询条件
        QueryWrapper<Application> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        // 模糊查询应用code
        queryWrapper.like(StringUtils.isNotBlank(form.getCode()), "code", form.getCode());
        // 模糊查询应用名称
        queryWrapper.like(StringUtils.isNotBlank(form.getName()), "name", form.getName());
        // 是否只显示我收藏的应用
        if (BooleanUtils.isTrue(form.getIsMyFavorite())){
            List<String> appCodeList = userFavoriteApplicationService.getUserFavoriteApplicationList(operator);
            queryWrapper.in(CollectionUtils.isNotEmpty(appCodeList),"code", appCodeList);
        }

        IPage<Application> page = applicationService.page(pageRequest, queryWrapper);

        IPage<ApplicationVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(convertToVO(page.getRecords(), operator));

        return voPage;
    }

    private List<ApplicationVO> convertToVO(List<Application> list, String account){
        Map<String, User> userMap = userService.getAccountUserMap();
        Map<String, Department> departmentMap = departmentService.getDepartmentMap();
        Set<String> appCodeSet = new HashSet<>(userFavoriteApplicationService.getUserFavoriteApplicationList(account));

        return list.stream().map(item -> {

            ApplicationVO vo = new ApplicationVO();
            BeanUtils.copyProperties(item, vo);
            User owner = userMap.get(item.getOwner());
            vo.setOwnerName(Objects.isNull(owner) ? item.getOwner() : owner.getUsername());
            Department department = departmentMap.get(item.getDepartment());
            vo.setDepartmentName(Objects.isNull(department) ? item.getDepartment() : department.getFullDepartmentName());
            // 是否是个人收藏的应用
            vo.setIsMyFavorite(appCodeSet.contains(item.getCode()));
            return vo;

        }).collect(Collectors.toList());
    }

    /**
     * 创建/取消用户收藏的应用
     */
    public void processMyFavoriteApplication(String appCode, String account, Boolean isFavorite){

        if (isFavorite){
            UserFavoriteApplication userFavoriteApplication = new UserFavoriteApplication();
            userFavoriteApplication.setAppCode(appCode);
            userFavoriteApplication.setUser(account);
            userFavoriteApplicationService.save(userFavoriteApplication);
        } else {
            QueryWrapper<UserFavoriteApplication> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("app_code", appCode);
            queryWrapper.eq("user", account);
            userFavoriteApplicationService.remove(queryWrapper);
        }
    }

    public boolean isApplicationExist(ApplicationForm.ApplicationExistForm form){
        QueryWrapper<Application> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", form.getCode());
        return applicationService.exists(queryWrapper);
    }

    // -----------------------应用资源关系相关的操作-------------------------------

    public List<SelectVO> getSelectableApplicationResourceList(ApplicationForm.ApplicationResourceQueryForm form){
        QueryWrapper<ApplicationResourceRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_code", form.getAppCode());
        queryWrapper.eq("env", form.getEnv());
        if (StringUtils.isNotBlank(form.getResourceType())){
            queryWrapper.eq("resource_type", form.getResourceType());
        }
        List<ApplicationResourceRelation> list = applicationResourceRelationService.list(queryWrapper);
        return transformSelectableApplicationResource(list, form.getResourceType());
    }

    private List<SelectVO> transformSelectableApplicationResource(List<ApplicationResourceRelation> list, String resourceType){
        Map<String, Server> serverMap = serverService.getServerMap();
        List<SelectVO> voList = new ArrayList<>();

        for (ApplicationResourceRelation item: list) {
            SelectVO vo = new SelectVO();

            if (resourceType.equals(ResourceTypeEnum.SERVER.name())){
                Server server = serverMap.get(item.getResourceId());
                if (Objects.nonNull(server) && server.getStatus() != 0){
                    // 发布到虚拟机的时候, IP是前端传过来的, 一般是内网
                    String ip = server.getPrivateIp();
                    String label = MessageFormat.format("{0}({1})", server.getInstanceName(), ip);
                    vo.setValue(ip);
                    vo.setLabel(label);
                    voList.add(vo);
                }
            }
        }

        return voList;
    }


    public IPage<ApplicationResourceVO> getPageableApplicationResourceList(ApplicationForm.ApplicationResourceQueryForm form){

        Page<ApplicationResourceRelation> pageRequest = new Page<>(form.getPageNum(), form.getPageSize());

        QueryWrapper<ApplicationResourceRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_code", form.getAppCode());
        queryWrapper.eq("env", form.getEnv());
        if (StringUtils.isNotBlank(form.getResourceType())){
            queryWrapper.eq("resource_type", form.getResourceType());
        }
        IPage<ApplicationResourceRelation> page = applicationResourceRelationService.page(pageRequest, queryWrapper);

        IPage<ApplicationResourceVO> voPage = new Page<>(pageRequest.getCurrent(), pageRequest.getSize(), pageRequest.getTotal());

        if(ResourceTypeEnum.SERVER.name().equals(form.getResourceType())){
            voPage.setRecords(transformApplicationServer(page.getRecords()));
        }

        return voPage;
    }

    public List<ApplicationResourceVO> transformApplicationServer(List<ApplicationResourceRelation> list){

        Map<String, Server> serverMap = serverService.getServerMap();

        List<ApplicationResourceVO> retList = new ArrayList<>();

        for (ApplicationResourceRelation item : list){
            Server server = serverMap.get(item.getResourceId());

            if (Objects.isNull(serverMap.get(item.getResourceId()))){
                continue;
            }

            ApplicationResourceVO vo = new ApplicationResourceVO();

            vo.setInstanceId(item.getResourceId());

            vo.setInstanceName(server.getInstanceName());
            String ip = MessageFormat.format("{0}({1})", server.getPrivateIp(), server.getPublicIp());
            vo.setIp(ip);
            vo.setPrivateIp(server.getPrivateIp());
            vo.setComment(server.getComment());
            vo.setCreateTime(server.getCreateTime());

            retList.add(vo);
        }
        return retList;
    }


    public void createApplicationResourceRelation(ApplicationForm.ApplicationResourceCreateOrDeleteForm form, String operator){
        if (permissionUtil.noPermission(operator, "cmdb:application:resource:server:edit")){
            throw new ServiceException("用户无权限: cmdb:application:resource:server:edit");
        }
        List<ApplicationResourceRelation> list = applicationAdapter.convertToApplicationResourceRelation(form, operator);
        applicationResourceRelationService.saveBatch(list);
    }

    /**
     * 删除应用绑定资源关系
     */
    public void deleteApplicationResourceRelation(ApplicationForm.ApplicationResourceCreateOrDeleteForm form, String operator){
        if (permissionUtil.noPermission(operator, "cmdb:application:resource:server:edit")){
            throw new ServiceException("用户无权限: cmdb:application:resource:server:edit");
        }
        List<ApplicationResourceRelation> list = applicationAdapter.convertToApplicationResourceRelation(form, operator);

        QueryWrapper<ApplicationResourceRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_code", form.getAppCode());
        queryWrapper.eq("env", form.getEnv());
        queryWrapper.eq("resource_type", form.getResourceType());
        queryWrapper.in("resource_id", form.getInstanceIdList());

        applicationResourceRelationService.remove(queryWrapper);

        log.info("用户[{}]删除应用[{}]的资源关系, 资源类型: {}, 环境: {}, 资源ID: {}", operator, form.getAppCode(),
                form.getResourceType(), form.getEnv(), JSON.toJSONString(form.getInstanceIdList()));
    }


    // -----------------------应用发布配置相关的操作-------------------------------

    private void createApplicationDeployConfig(String appCode){

        applicationDeployConfigService.saveBatch(Arrays.stream(EnvironmentEnum.values()).map(env -> { ApplicationDeployConfig config = new ApplicationDeployConfig();
            config.setAppCode(appCode);
            config.setEnv(env.getCode());
        return config;}).toList());

    }

    @Transactional(rollbackFor = Exception.class)
    public void updateApplicationDeployConfig(ApplicationConfigForm.ApplicationDeployConfigForm form, String operator){
        // 权限校验
        if (permissionUtil.noPermission(operator, "cmdb:application:cicd:config:edit")){
            throw new ServiceException("用户无权限: cmdb:application:cicd:config:edit");
        }

        List<ApplicationDeployConfig> list = applicationAdapter.convertToApplicationDeployConfig(form);
        for (ApplicationDeployConfig config : list) {
            UpdateWrapper<ApplicationDeployConfig> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("app_code", config.getAppCode());
            updateWrapper.eq("env", config.getEnv());
            applicationDeployConfigService.update(config, updateWrapper);
            asyncUtil.doTask(()-> recordApplicationDeployConfigChange(config, operator));
        }
    }

    /**
     * 记录应用发布配置变更
     */
    private void recordApplicationDeployConfigChange(ApplicationDeployConfig config, String operator){
        // 查询之前的配置
        ApplicationDeployConfig existConfig = applicationDeployConfigService.getByAppcodeAndEnvCode(config.getAppCode(), config.getEnv());

        boolean isEdit = false;
        StringBuilder content = new StringBuilder(String.format("应用: [%s], 环境: [%s] 发布配置修改; ", config.getAppCode(), config.getEnv()));
        // Gitlab地址
        if (!Objects.equals(existConfig.getGit(), config.getGit())){
            content.append(String.format("Git地址从[%s]修改为[%s]; ", existConfig.getGit(), config.getGit()));
            isEdit = true;
        }
        // 健康检查地址
        if (!Objects.equals(existConfig.getHealthCheckUri(), config.getHealthCheckUri())){
            isEdit = true;
            content.append(String.format("HealthCheckUri从[%s]修改为[%s]; ", existConfig.getHealthCheckUri(), config.getHealthCheckUri()));
        }
        // 制品名称
        if (!Objects.equals(existConfig.getArtifactName(), config.getArtifactName())){
            isEdit = true;
            content.append(String.format("ArtifactName从[%s]修改为[%s]; ", existConfig.getArtifactName(), config.getArtifactName()));
        }
        // 制品类型
        if (!Objects.equals(existConfig.getArtifactType(), config.getArtifactType())){
            isEdit = true;
            content.append(String.format("ArtifactType从[%s]修改为[%s]; ", existConfig.getArtifactType(), config.getArtifactType()));
        }
        // 制品路径
        if (!Objects.equals(existConfig.getArtifactPath(), config.getArtifactPath())){
            isEdit = true;
            content.append(String.format("ArtifactPath从[%s]修改为[%s]; ", existConfig.getArtifactPath(), config.getArtifactPath()));
        }
        // 监听端口
        if (!Objects.equals(existConfig.getPort(), config.getPort())){
            isEdit = true;
            content.append(String.format("监听端口(port)从[%s]修改为[%s]; ", existConfig.getPort(), config.getPort()));
        }
        // 探活方式
        if (!Objects.equals(existConfig.getProbeType(), config.getProbeType())){
            isEdit = true;
            content.append(String.format("探活方式从[%s]修改为[%s]; ", existConfig.getProbeType(), config.getProbeType()));
        }
        // BuildExtraArgs
        if (!Objects.equals(existConfig.getBuildExtraArgs(), config.getBuildExtraArgs())){
            isEdit = true;
            content.append(String.format("build_extra_args从[%s]修改为[%s]; ", existConfig.getBuildExtraArgs(), config.getBuildExtraArgs()));
        }
        // Profile
        if (!Objects.equals(existConfig.getProfile(), config.getProfile())){
            isEdit = true;
            content.append(String.format("Profile从[%s]修改为[%s]; ", existConfig.getProfile(), config.getProfile()));
        }
        // 企业微信Token
        if (!Objects.equals(existConfig.getWorkWeixinToken(), config.getWorkWeixinToken())){
            isEdit = true;
            content.append(String.format("getWorkWeixinToken从[%s]修改为[%s]; ", existConfig.getWorkWeixinToken(), config.getWorkWeixinToken()));
        }
        // 初始延迟秒数
        if (!Objects.equals(existConfig.getInitialDelaySeconds(), config.getInitialDelaySeconds())){
            isEdit = true;
            content.append(String.format("getInitialDelaySeconds从[%s]修改为[%s]; ", existConfig.getInitialDelaySeconds(), config.getInitialDelaySeconds()));
        }
        // Git提交自动部署
        if (!Objects.equals(existConfig.getAutoDeployOnGitCommit(), config.getAutoDeployOnGitCommit())){
            isEdit = true;
            content.append(String.format("AutoDeployOnGitCommit从[%s]修改为[%s]; ", existConfig.getAutoDeployOnGitCommit(), config.getAutoDeployOnGitCommit()));
        }
        // 运行时版本
        if (!Objects.equals(existConfig.getRuntimeVersion(), config.getRuntimeVersion())){
            isEdit = true;
            content.append(String.format("RuntimeVersion从[%s]修改为[%s]; ", existConfig.getRuntimeVersion(), config.getRuntimeVersion()));
        }
        // 容器化
        if (!Objects.equals(existConfig.getContainerized(), config.getContainerized())){
            isEdit = true;
            content.append(String.format("Containerized从[%s]修改为[%s]; ", existConfig.getContainerized(), config.getContainerized()));
        }
        // RunExtraArgs
        if (!Objects.equals(existConfig.getRunExtraArgs(), config.getRunExtraArgs())){
            isEdit = true;
            content.append(String.format("RunExtraArgs从[%s]修改为[%s]; ", existConfig.getRunExtraArgs(), config.getRunExtraArgs()));
        }
        // KubernetesReplicas
        if (!Objects.equals(existConfig.getKubernetesReplicas(), config.getKubernetesReplicas())){
            isEdit = true;
            content.append(String.format("KubernetesReplicas 从[%s]修改为[%s]; ", existConfig.getKubernetesReplicas(), config.getKubernetesReplicas()));
        }
        // KubernetesLimitCpu
        if (!Objects.equals(existConfig.getKubernetesLimitCpu(), config.getKubernetesLimitCpu())){
            isEdit = true;
            content.append(String.format("KubernetesLimitCpu 从[%s]修改为[%s]; ", existConfig.getKubernetesLimitCpu(), config.getKubernetesLimitCpu()));
        }
        // KubernetesLimitMemory
        if (!Objects.equals(existConfig.getKubernetesLimitMemory(), config.getKubernetesLimitMemory())){
            isEdit = true;
            content.append(String.format("KubernetesLimitMemory 从[%s]修改为[%s]; ", existConfig.getKubernetesLimitMemory(), config.getKubernetesLimitMemory()));
        }
        // KubernetesScheduleStrategy
        if (!Objects.equals(existConfig.getKubernetesScheduleStrategy(), config.getKubernetesScheduleStrategy())){
            isEdit = true;
            content.append(String.format("KubernetesScheduleStrategy 从[%s]修改为[%s]; ", existConfig.getKubernetesScheduleStrategy(), config.getKubernetesScheduleStrategy()));
        }
        // DockerfileTemplateName
        if (!Objects.equals(existConfig.getDockerfileTemplateName(), config.getDockerfileTemplateName())){
            isEdit = true;
            content.append(String.format("DockerfileTemplateName 从[%s]修改为[%s]; ", existConfig.getDockerfileTemplateName(), config.getDockerfileTemplateName()));
        }

        if (isEdit){
            OperationRecord record = new OperationRecord();
            record.setCreateUser(operator);
            record.setType(OperationTypeEnum.UPDATE_APPLICATION_DEPLOY_CONFIG.getType());
            record.setContent(content.toString());
            operationRecordService.save(record);
            try{
                workWechatSender.send(ZEUS_WORK_WECHAT_ROBOT_KEY,
                        JSON.toJSONString(record));
            } catch (Exception ignored){}
        }
    }

    public ApplicationDeployConfigVO getApplicationDeployConfigByAppCode(String appCode){
        ApplicationDeployConfigVO vo = new ApplicationDeployConfigVO();
        List<ApplicationDeployConfig> list = applicationDeployConfigService.getByAppcode(appCode);

        for (ApplicationDeployConfig config : list) {
            // 生产环境
            if (EnvironmentEnum.PROD.getCode().equals(config.getEnv())){
                // 通用配置
                ApplicationConfigForm.ApplicationDeployConfigCommon common = new ApplicationConfigForm.ApplicationDeployConfigCommon();
                BeanUtils.copyProperties(config, common);
                vo.setCommon(common);
                ApplicationConfigForm.ApplicationDeployConfigByEnv byEnv = new ApplicationConfigForm.ApplicationDeployConfigByEnv();
                BeanUtils.copyProperties(config, byEnv);
                vo.setProd(byEnv);
            } else if (EnvironmentEnum.PRE.getCode().equals(config.getEnv())){
                ApplicationConfigForm.ApplicationDeployConfigByEnv byEnv = new ApplicationConfigForm.ApplicationDeployConfigByEnv();
                BeanUtils.copyProperties(config, byEnv);
                vo.setPre(byEnv);
            } else if (EnvironmentEnum.TEST.getCode().equals(config.getEnv())){
                ApplicationConfigForm.ApplicationDeployConfigByEnv byEnv = new ApplicationConfigForm.ApplicationDeployConfigByEnv();
                BeanUtils.copyProperties(config, byEnv);
                vo.setTest(byEnv);
            }
        }

        return vo;
    }

    /**
     * 获取一个随机的不重复的nodePort
     */
    public Integer getAvailableNodePort(String envCode) {
        Integer port = null;
        // 生产环境/预发环境 共用相同的集群, 通过创建单独的namespace来解决, 所以这几个环境的NodePort端口要在一起计算
        Set<Integer> existPortSet = new HashSet<>();

        if (EnvironmentEnum.TEST.getCode().equals(envCode)){
            existPortSet = applicationDeployConfigService.getTotalNodePortByEnvCode(envCode);
        }
        if (
                EnvironmentEnum.PROD.getCode().equals(envCode)
                        || EnvironmentEnum.PRE.getCode().equals(envCode) ){
            existPortSet.addAll(applicationDeployConfigService.getTotalNodePortByEnvCode(EnvironmentEnum.PROD.getCode()));
            existPortSet.addAll(applicationDeployConfigService.getTotalNodePortByEnvCode(EnvironmentEnum.PRE.getCode()));
        }

        //kubernetes集群的默认nodePort范围是30000-32767, 创建集群的时候应该调整大一点
        boolean verified = false;
        while (!verified){
            port = RandomUtil.randomInt(30000, 32767);
            if (!existPortSet.contains(port)){
                verified = true;
            }
        }
        
        return port;
    }
}
