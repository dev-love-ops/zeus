package com.wufeiqun.zeus.biz.system;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wufeiqun.zeus.biz.system.entity.RoleForm;
import com.wufeiqun.zeus.biz.system.entity.UserForm;
import com.wufeiqun.zeus.biz.system.entity.UserVO;
import com.wufeiqun.zeus.common.entity.SelectVO;
import com.wufeiqun.zeus.common.exception.ServiceException;
import com.wufeiqun.zeus.dao.Role;
import com.wufeiqun.zeus.dao.User;
import com.wufeiqun.zeus.service.IRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleFacade {
    private final IRoleService roleService;

    public List<SelectVO> getSelectableRoleList(){
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("code", "name");

        return roleService.list(queryWrapper).stream().map(user -> {
            SelectVO vo = new SelectVO();
            vo.setValue(user.getCode());
            vo.setLabel(user.getName());
            return vo;
        }).collect(Collectors.toList());
    }

    public IPage<Role> getPageableRoleList(RoleForm.RoleSearchForm form){
        Page<Role> pageRequest = new Page<>(form.getPageNum(), form.getPageSize());

        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        return roleService.page(pageRequest, queryWrapper);
    }

    public void deleteRole(RoleForm.DeleteRoleForm form, String operator){
        // 校验该角色下是否有用户绑定
//        List<String> userList = userRoleRelationService.getRoleUserList(form.getId());
//        if (CollectionUtil.isNotEmpty(userList)){
//            String user = String.join(",", userList);
//            throw new ServiceException(String.format("无法删除, 用户[%s]在使用, 请先解绑!", user));
//        }
        // 校验该角色是否有绑定菜单
//        List<Long> menuIdList = roleMenuRelationService.getRoleMenuIdList(form.getId());
//        if (CollectionUtil.isNotEmpty(menuIdList)){
//            throw new ServiceException("无法删除, 请先解绑该角色的所有菜单!");
//        }
        // 打印日志
        roleService.removeById(form.getId());
        log.info("user [{}] delete role [{}]!", operator, form);
    }

}
