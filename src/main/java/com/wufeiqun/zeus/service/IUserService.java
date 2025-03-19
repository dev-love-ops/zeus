package com.wufeiqun.zeus.service;

import com.wufeiqun.zeus.dao.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

/**
 * <p>
 * 用户 服务类
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
public interface IUserService extends IService<User> {
    Set<String> getUserPermissionCodeList(String account);
    Set<Long> getUserMenuIdList(String account);
}
