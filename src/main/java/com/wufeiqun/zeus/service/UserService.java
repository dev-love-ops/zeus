package com.wufeiqun.zeus.service;

import com.wufeiqun.zeus.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户 服务类
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-17
 */
public interface UserService extends IService<User> {
    String test();
}
