package com.wufeiqun.zeus.service;

import com.wufeiqun.zeus.dao.UserFavoriteApplication;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户收藏的应用 服务类
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
public interface IUserFavoriteApplicationService extends IService<UserFavoriteApplication> {
    List<String> getUserFavoriteApplicationList(String account);
}
