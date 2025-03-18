package com.wufeiqun.zeus.mapper;

import com.wufeiqun.zeus.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;


/**
 * <p>
 * 用户 Mapper 接口
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-17
 */
public interface UserMapper extends BaseMapper<User> {
    int countByName(String name);
}

