package com.wufeiqun.zeus.service.impl;

import com.wufeiqun.zeus.entity.User;
import com.wufeiqun.zeus.mapper.UserMapper;
import com.wufeiqun.zeus.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
