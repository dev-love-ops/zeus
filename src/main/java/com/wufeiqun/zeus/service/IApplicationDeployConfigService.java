package com.wufeiqun.zeus.service;

import com.wufeiqun.zeus.dao.ApplicationDeployConfig;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 应用发布相关配置 服务类
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
public interface IApplicationDeployConfigService extends IService<ApplicationDeployConfig> {
    List<ApplicationDeployConfig> getByAppcode(String appCode);
    ApplicationDeployConfig getByAppcodeAndEnvCode(String appCode, String envCode);
    Set<Integer> getTotalNodePortByEnvCode(String envCode);
}
