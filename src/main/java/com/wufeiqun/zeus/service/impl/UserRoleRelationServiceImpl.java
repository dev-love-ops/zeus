package com.wufeiqun.zeus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wufeiqun.zeus.dao.UserRoleRelation;
import com.wufeiqun.zeus.dao.UserRoleRelationMapper;
import com.wufeiqun.zeus.service.IUserRoleRelationService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户角色关系表 服务实现类
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
@Service
public class UserRoleRelationServiceImpl extends ServiceImpl<UserRoleRelationMapper, UserRoleRelation> implements IUserRoleRelationService {
}
