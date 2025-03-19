package com.wufeiqun.zeus.service.impl;

import com.wufeiqun.zeus.dao.User;
import com.wufeiqun.zeus.dao.UserMapper;
import com.wufeiqun.zeus.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public Set<String> getUserPermissionCodeList(String account) {
        return baseMapper.getUserPermissionCodeList(account);
    }

    @Override
    public Set<Long> getUserMenuIdList(String account) {
        return baseMapper.getUserMenuIdList(account);
    }

}
