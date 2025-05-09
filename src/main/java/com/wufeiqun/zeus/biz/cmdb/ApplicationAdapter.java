package com.wufeiqun.zeus.biz.cmdb;

import com.wufeiqun.zeus.biz.cicd.enums.ArtifactTypeEnum;
import com.wufeiqun.zeus.biz.cicd.enums.ServiceProbeTypeEnum;
import com.wufeiqun.zeus.biz.cmdb.entity.ApplicationConfigForm;
import com.wufeiqun.zeus.biz.cmdb.entity.ApplicationForm;
import com.wufeiqun.zeus.biz.cmdb.enums.EnvironmentEnum;
import com.wufeiqun.zeus.common.exception.ServiceException;
import com.wufeiqun.zeus.dao.ApplicationDeployConfig;
import com.wufeiqun.zeus.dao.ApplicationResourceRelation;
import com.wufeiqun.zeus.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wufeiqun.zeus.common.constant.GlobalConstant.CMDB_APPCODE_PREFIX;


/**
 * @author wufeiqun
 * @date 2022-07-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationAdapter {

    private final IUserService userService;
    private final IDepartmentService departmentService;
    private final IServerService serverService;
    private final IApplicationDeployConfigService applicationDeployConfigService;
    private final IUserFavoriteApplicationService userFavoriteApplicationService;



    /**
     *
     *
     *
     */



    public List<ApplicationResourceRelation> convertToApplicationResourceRelation(ApplicationForm.ApplicationResourceCreateOrDeleteForm form, String operator){

        return form.getInstanceIdList().stream().map(item -> {
            ApplicationResourceRelation arr = new ApplicationResourceRelation();

            arr.setAppCode(form.getAppCode());
            arr.setEnv(form.getEnv());
            arr.setResourceType(form.getResourceType());
            arr.setResourceId(item);
            arr.setCreateUser(operator);

            return arr;
        }).collect(Collectors.toList());
    }

    public List<ApplicationDeployConfig> convertToApplicationDeployConfig(ApplicationConfigForm.ApplicationDeployConfigForm form){
        // 参数校验
        applicationDeployConfigValidator(form);
        List<ApplicationDeployConfig> list = new ArrayList<>();
        // 生产环境
        if (Objects.nonNull(form.getProd())){
            list.add(applicationDeployConfigConverter(form.getAppCode(), EnvironmentEnum.PROD.getCode(), form.getCommon(), form.getProd()));
        }
        // 预发环境
        if (Objects.nonNull(form.getPre())){
            list.add(applicationDeployConfigConverter(form.getAppCode(), EnvironmentEnum.PRE.getCode(), form.getCommon(), form.getPre()));
        }
        // 测试环境
        if (Objects.nonNull(form.getTest())){
            list.add(applicationDeployConfigConverter(form.getAppCode(), EnvironmentEnum.TEST.getCode(), form.getCommon(), form.getTest()));
        }


        return list;
    }

    private ApplicationDeployConfig applicationDeployConfigConverter(String appCode, String env,
        ApplicationConfigForm.ApplicationDeployConfigCommon common, ApplicationConfigForm.ApplicationDeployConfigByEnv byEnv){

        ApplicationDeployConfig config = new ApplicationDeployConfig();
        // --------------------------通用配置--------------------------
        config.setAppCode(appCode);
        config.setEnv(env);
        config.setArtifactType(common.getArtifactType());
        config.setGit(common.getGit().trim());
        config.setArtifactName(common.getArtifactName().trim());
        config.setArtifactPath(common.getArtifactPath().trim());
        // 没有探活方式的应用类型
        if (ArtifactTypeEnum.JARAPI.getType().equals(common.getArtifactType())
                || ArtifactTypeEnum.STATICFE.getType().equals(common.getArtifactType())
        ){
            config.setProbeType(ServiceProbeTypeEnum.NONE.getType());
        }else {
            config.setProbeType(common.getProbeType());
        }
        config.setHealthCheckUri(common.getHealthCheckUri());
        config.setWorkWeixinToken(common.getWorkWeixinToken());
        config.setInitialDelaySeconds(common.getInitialDelaySeconds());
//        config.setAutoDeployOnGitCommit(common.getAutoDeployOnGitCommit());
        config.setRuntimeVersion(common.getRuntimeVersion());
//        config.setContainerized(common.getContainerized());

        // 单环境配置
        config.setPort(byEnv.getPort());
        config.setBuildExtraArgs(byEnv.getBuildExtraArgs());
        config.setBuildBranch(byEnv.getBuildBranch());
        config.setProfile(byEnv.getProfile());
        config.setRunExtraArgs(byEnv.getRunExtraArgs());
        config.setKubernetesNamespace(byEnv.getKubernetesNamespace());
        config.setDockerfileTemplateName(byEnv.getDockerfileTemplateName());
        config.setKubernetesReplicas(byEnv.getKubernetesReplicas());
        config.setKubernetesLimitCpu(byEnv.getKubernetesLimitCpu());
        config.setKubernetesLimitMemory(byEnv.getKubernetesLimitMemory());
        config.setKubernetesNodePort(byEnv.getKubernetesNodePort());
        config.setKubernetesScheduleStrategy(byEnv.getKubernetesScheduleStrategy());
        config.setPrometheusScrape(byEnv.getPrometheusScrape());
        config.setPrometheusPath(byEnv.getPrometheusPath());

        return config;
    }

    /**
     * 应用发布配置变更校验
     */
    private void applicationDeployConfigValidator(ApplicationConfigForm.ApplicationDeployConfigForm form){
        // 校验gitlab地址合法性, 填写git协议的地址而不是HTTPS协议的地址
        if (!form.getCommon().getGit().startsWith("git")){
            log.warn("ApplicationAdapter.applicationDeployConfigValidator: " + form.getCommon().getGit());
            throw new ServiceException("请填写正确的git地址(git协议地址, 不是HTTPS地址)");
        }
        // 校验制品路径
        if (StringUtils.isNotBlank(form.getCommon().getArtifactPath())){
            if (form.getCommon().getArtifactPath().startsWith("/") || form.getCommon().getArtifactPath().endsWith("/")){
                throw new ServiceException("请移除制品路径开头和结尾的斜杠");
            }
        }
        // 校验runtimeVersion, 不能为空
        if (StringUtils.isBlank(form.getCommon().getRuntimeVersion())){
            throw new ServiceException("运行时版本(runtimeVersion)不能为空");
        }

        // ----------------------------容器化相关的校验开始-------------------
        if (form.getCommon().getContainerized()){
            // CPU/内存限制
            if (form.getTest().getKubernetesLimitCpu() > 2.0){
                throw new ServiceException("测试环境容器CPU最大允许2核心");
            }
            if (form.getTest().getKubernetesLimitMemory() > 5120){
                throw new ServiceException("测试环境容器内存最大允许5120M");
            }
            if (form.getPre().getKubernetesLimitCpu() > 2.0){
                throw new ServiceException("预发环境CPU最大允许2核心");
            }
            if (form.getPre().getKubernetesLimitMemory() > 5120){
                throw new ServiceException("预发环境内存最大允许5120M");
            }
            if (form.getProd().getKubernetesLimitCpu() < 2.0 || form.getProd().getKubernetesLimitCpu() > 4.0){
                throw new ServiceException("生产环境CPU核心数范围为[2,4]");
            }
            if (form.getProd().getKubernetesLimitMemory() > 8192){
                throw new ServiceException("生产环境内存最大允许8192M");
            }

            //副本数的限制
            if (form.getTest().getKubernetesReplicas() > 1 || form.getPre().getKubernetesReplicas() > 1 ){
                throw new ServiceException("测试环境/预发环境/灰度环境的副本数只能为0或1个");
            }
            if (form.getProd().getKubernetesReplicas() > 4 || form.getProd().getKubernetesReplicas() < 2){
                throw new ServiceException("生产环境的副本数范围[2-4]");
            }

            ApplicationDeployConfig testConfig = applicationDeployConfigService.getByAppcodeAndEnvCode(form.getAppCode(), EnvironmentEnum.TEST.getCode());
            ApplicationDeployConfig preConfig = applicationDeployConfigService.getByAppcodeAndEnvCode(form.getAppCode(), EnvironmentEnum.PRE.getCode());
            ApplicationDeployConfig prodConfig = applicationDeployConfigService.getByAppcodeAndEnvCode(form.getAppCode(), EnvironmentEnum.PROD.getCode());
            // nodePort不允许修改, nodePort如果被其它的服务用到的话, 修改会有问题, 所以这里一旦设置就不允许修改了
            if (testConfig.getKubernetesNodePort() != 0){
                form.getTest().setKubernetesNodePort(null);
            }
            if (preConfig.getKubernetesNodePort() != 0){
                form.getPre().setKubernetesNodePort(null);
            }
            if (prodConfig.getKubernetesNodePort() != 0){
                form.getProd().setKubernetesNodePort(null);
            }

            // 命名空间必须配置, 不然就会发送到default命名空间了
            // 应用绑定命名空间(namespace)以后就不允许修改了, 修改会有问题
            if (StringUtils.isEmpty(form.getProd().getKubernetesNamespace()) || StringUtils.isEmpty(form.getPre().getKubernetesNamespace())
                    || StringUtils.isEmpty(form.getTest().getKubernetesNamespace()) ){
                throw new ServiceException("开启容器化以后, 命名空间必须配置(测试/预发/灰度/生产)");
            }

            if (StringUtils.isNotEmpty(testConfig.getKubernetesNamespace())){
                form.getTest().setKubernetesNamespace(null);
            }
            if (StringUtils.isNotEmpty(preConfig.getKubernetesNamespace())){
                form.getPre().setKubernetesNamespace(null);
            }

            if (StringUtils.isNotEmpty(prodConfig.getKubernetesNamespace())){
                form.getProd().setKubernetesNamespace(null);
            }

        }
        // ----------------------------容器化相关的校验结束-------------------


    }

    public void appCodeValidation(String appCode){
        String prefix = appCode.split("-")[0];
        if (!CMDB_APPCODE_PREFIX.contains(prefix)){
            throw new ServiceException("appCode必须以如下英文开头: " + CMDB_APPCODE_PREFIX);
        }
    }

}
