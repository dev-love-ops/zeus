package com.wufeiqun.zeus.biz.cicd;

import com.wufeiqun.zeus.biz.cicd.entity.CicdDeployContext;
import com.wufeiqun.zeus.biz.cicd.entity.CicdDeployForm;
import com.wufeiqun.zeus.biz.cicd.enums.ArtifactTypeEnum;
import com.wufeiqun.zeus.biz.cmdb.enums.EnvironmentEnum;
import com.wufeiqun.zeus.common.exception.ServiceException;
import com.wufeiqun.zeus.common.utils.PermissionUtil;
import com.wufeiqun.zeus.dao.ApplicationDeployConfig;
import com.wufeiqun.zeus.dao.CicdBuildRecord;
import com.wufeiqun.zeus.service.IApplicationDeployConfigService;
import com.wufeiqun.zeus.service.ICicdBuildRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import static com.wufeiqun.zeus.common.constant.RedisCacheKey.CICD_RATE_LIMIT_DEPLOY;


/**
 * @author wufeiqun
 * @date 2022-08-16
 * 发布预处理器, 参数校验, 前置任务等
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CicdDeployPreProcessor {
    private final IApplicationDeployConfigService applicationDeployConfigService;
    private final ICicdBuildRecordService cicdBuildRecordService;
    private final PermissionUtil permissionUtil;
    private final StringRedisTemplate redisTemplate;


    public CicdDeployContext prepareDeployContext(CicdDeployForm.RunDeployForm runDeployForm, String operator) {
        CicdDeployContext context = new CicdDeployContext();
        context.setRunDeployForm(runDeployForm);
        context.setOperator(operator);
        saveBuildRecordToContext(context);
        saveDeployConfigToContext(context);
        initDockerConfigToContext(context);
        return context;
    }

    /**
     * 1. 应用配置是否合理, 比如仓库/包路径等配置
     * 2. 用户权限是否合理等
     * @param context 参数
     * @param operator 用户
     */
    public void validate(CicdDeployContext context, String operator) {
        // 常规校验
        commonValid(context, operator);
        // 权限校验
        permissionValid(context, operator);
        // 发布限速, 5分钟内最多构建2次
        rateLimit(context, operator);
    }
    private void rateLimit(CicdDeployContext context, String operator){
        // 查询已经构建的次数
        Set<String> keys = redisTemplate.keys(String.format("%s:%s:*", CICD_RATE_LIMIT_DEPLOY, operator));
        if (keys.size() >= 10){
            throw new ServiceException("部署太频繁, 请3分钟后再试!");
        }
        redisTemplate.opsForValue().set(String.format("%s:%s:%s:%s:%s", CICD_RATE_LIMIT_DEPLOY, operator,
                context.getRunDeployForm().getAppCode(), context.getRunDeployForm().getEnvCode(),
                new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())), "OK", 180, java.util.concurrent.TimeUnit.SECONDS);
    }

    private void commonValid(CicdDeployContext context, String operator){

        // 灰度功能校验, 暂时只开放灰度功能给前端, 等后端梳理完成以后再支持后端, 以防影响生产环境
//        if (EnvironmentEnum.GRAY.getCode().equals(context.getRunDeployForm().getEnvCode())){
//            if (!CicdBuildPkgTypeEnum.STATICFE.getType().equals(context.getApplicationDeployConfig().getPkgType())){
//                throw new ServiceException("灰度发布暂时只支持前端应用!");
//            }
//        }

        // jar-api类型的应用无需发布, 执行完构建就表示已经推送到私服
        if (ArtifactTypeEnum.JARAPI.getType().equals(context.getApplicationDeployConfig().getArtifactType())){
                throw new ServiceException("jar-api类型应用无需发布, 构建完成就表示已经推送到私服!");
        }
    }

    private void permissionValid(CicdDeployContext context, String operator){
        if (EnvironmentEnum.PROD.getCode().equals(context.getRunDeployForm().getEnvCode())){
            if (permissionUtil.noPermission(operator, "cicd:quick:prod:deploy")){
                throw new ServiceException("用户无权限: cicd:quick:prod:deploy");
            }
        }
    }


    /**
     * 快速发布模式下发布的逻辑是:
     * 将指定环境/指定appCode的最近一次构建成功的记录发布
     */
    private void saveBuildRecordToContext(CicdDeployContext context) {
        CicdBuildRecord record = cicdBuildRecordService.getById(context.getRunDeployForm().getBuildRecordId());
        context.setBuildRecord(record);
    }

    private void saveDeployConfigToContext(CicdDeployContext context) {
        ApplicationDeployConfig config = applicationDeployConfigService.getByAppcodeAndEnvCode(
                context.getRunDeployForm().getAppCode(),
                context.getRunDeployForm().getEnvCode()
        );

        if (Objects.isNull(config)){
            throw new ServiceException("应用发布配置异常!");
        }

        context.setApplicationDeployConfig(config);
    }
    private void initDockerConfigToContext(CicdDeployContext context) {
        // 测试环境暂时使用公共的命名空间
        if (EnvironmentEnum.TEST.getCode().equals(context.getRunDeployForm().getEnvCode())){
            if (StringUtils.isEmpty(context.getApplicationDeployConfig().getKubernetesNamespace())){
                context.getApplicationDeployConfig().setKubernetesNamespace("test");
            }
        }
    }
}
