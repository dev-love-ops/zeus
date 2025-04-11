package com.wufeiqun.zeus.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Set;


/**
 * <p>
 * 用户 Mapper 接口
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
public interface UserMapper extends BaseMapper<User> {
    Set<String> getUserPermissionCodeList(String account);
    Set<Long> getUserMenuIdList(String account);
    List<Role> getUserRoleList(String account);
}

