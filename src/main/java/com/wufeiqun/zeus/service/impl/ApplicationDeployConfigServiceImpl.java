package com.wufeiqun.zeus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wufeiqun.zeus.dao.ApplicationDeployConfig;
import com.wufeiqun.zeus.dao.ApplicationDeployConfigMapper;
import com.wufeiqun.zeus.service.IApplicationDeployConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 应用发布相关配置 服务实现类
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
@Service
public class ApplicationDeployConfigServiceImpl extends ServiceImpl<ApplicationDeployConfigMapper, ApplicationDeployConfig> implements IApplicationDeployConfigService {

    @Override
    public List<ApplicationDeployConfig> getByAppcode(String appCode) {
        QueryWrapper<ApplicationDeployConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_code", appCode);
        return this.list(queryWrapper);
    }

    @Override
    public ApplicationDeployConfig getByAppcodeAndEnvCode(String appCode, String envCode) {
        QueryWrapper<ApplicationDeployConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_code", appCode).eq("env", envCode);
        return this.getOne(queryWrapper);
    }

    @Override
    public Set<Integer> getTotalNodePortByEnvCode(String envCode) {
        QueryWrapper<ApplicationDeployConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("env", envCode);
        return this.list(queryWrapper).stream().map(ApplicationDeployConfig::getKubernetesNodePort).collect(Collectors.toSet());
    }
}
