package com.wufeiqun.zeus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wufeiqun.zeus.dao.UserFavoriteApplication;
import com.wufeiqun.zeus.dao.UserFavoriteApplicationMapper;
import com.wufeiqun.zeus.service.IUserFavoriteApplicationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户收藏的应用 服务实现类
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
@Service
public class UserFavoriteApplicationServiceImpl extends ServiceImpl<UserFavoriteApplicationMapper, UserFavoriteApplication> implements IUserFavoriteApplicationService {

    @Override
    public List<String> getUserFavoriteApplicationList(String account) {
        QueryWrapper<UserFavoriteApplication> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user", account);
        return this.list(queryWrapper).stream().map(UserFavoriteApplication::getAppCode).collect(Collectors.toList());
    }
}
