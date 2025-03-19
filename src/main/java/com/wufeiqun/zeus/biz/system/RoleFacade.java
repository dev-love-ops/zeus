package com.wufeiqun.zeus.biz.system;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wufeiqun.zeus.biz.system.entity.RoleForm;
import com.wufeiqun.zeus.biz.system.entity.UserForm;
import com.wufeiqun.zeus.biz.system.entity.UserVO;
import com.wufeiqun.zeus.common.entity.SelectVO;
import com.wufeiqun.zeus.common.exception.ServiceException;
import com.wufeiqun.zeus.common.utils.DifferenceUtil;
import com.wufeiqun.zeus.dao.Role;
import com.wufeiqun.zeus.dao.RoleMenuRelation;
import com.wufeiqun.zeus.dao.User;
import com.wufeiqun.zeus.dao.UserRoleRelation;
import com.wufeiqun.zeus.service.IRoleMenuRelationService;
import com.wufeiqun.zeus.service.IRoleService;
import com.wufeiqun.zeus.service.IUserRoleRelationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleFacade {
    private final IRoleService roleService;
    private final IRoleMenuRelationService roleMenuRelationService;
    private final IUserRoleRelationService userRoleRelationService;

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
        List<String> userList = getRoleUserList(form.getId());
        if (CollectionUtil.isNotEmpty(userList)){
            String user = String.join(",", userList);
            throw new ServiceException(String.format("无法删除, 用户[%s]在使用, 请先解绑!", user));
        }
        // 校验该角色是否有绑定菜单
        List<Long> menuIdList = getRoleMenuIdList(form.getId());
        if (CollectionUtil.isNotEmpty(menuIdList)){
            throw new ServiceException("无法删除, 请先解绑该角色的所有菜单!");
        }
        // 打印日志
        roleService.removeById(form.getId());
        log.info("user [{}] delete role [{}]!", operator, form);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateRoleMenuRelation(RoleForm.UpdateRoleMenuForm form, String operator){
        // 更新角色名称和备注等信息
        Role role = new Role();
        BeanUtil.copyProperties(form, role);
        role.setUpdateUser(operator);
        roleService.updateById(role);
        // 获取当前角色已经绑定的菜单ID的列表
        List<Long> roleExistMenuIdList = getRoleMenuIdList(form.getId());
        // 获取该角色最新的菜单ID列表
        List<Long> roleNewMenuIdList = form.getMenuIdList();
        List<Long> needDelete = DifferenceUtil.differenceObject(roleExistMenuIdList, roleNewMenuIdList);
        List<Long> needInsert = DifferenceUtil.differenceObject(roleNewMenuIdList, roleExistMenuIdList);
        // 删除角色菜单关联
        QueryWrapper<RoleMenuRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", form.getId());
        queryWrapper.in("menu_id", needDelete);
        roleMenuRelationService.remove(queryWrapper);
        // 新增最新的关联数据
        List<RoleMenuRelation> list = needInsert.stream().map((menuId) -> {
            RoleMenuRelation relation = new RoleMenuRelation();
            relation.setRoleId(form.getId());
            relation.setMenuId(menuId);
            relation.setUpdateUser(operator);
            return relation;
        }).toList();
        roleMenuRelationService.saveBatch(list);

        log.info("用户[{}]修改角色[{}]的菜单权限, 新增了: {}, 删除了: {}", operator, role.getCode(), JSON.toJSONString(needInsert), JSON.toJSONString(needDelete));
    }


    private List<String> getRoleUserList(Long roleId){
        QueryWrapper<UserRoleRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("account");
        queryWrapper.eq("role_id", roleId);
        return userRoleRelationService.list(queryWrapper).stream().map(UserRoleRelation::getAccount).collect(Collectors.toList());
    }

    private List<Long> getRoleMenuIdList(Long roleId){
        QueryWrapper<RoleMenuRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("menu_id");
        queryWrapper.eq("role_id", roleId);
        return roleMenuRelationService.list(queryWrapper).stream().map(RoleMenuRelation::getMenuId).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void createRoleWithRoleMenuRelation(RoleForm.CreateRoleForm form, String operator){
        Role role = new Role();
        BeanUtils.copyProperties(form, role);
        roleService.save(role);

        if (!CollectionUtils.isEmpty(form.getMenuIdList())){
            List<RoleMenuRelation> list  = form.getMenuIdList().stream().map((menuId) -> {
                RoleMenuRelation relation = new RoleMenuRelation();
                relation.setRoleId(role.getId());
                relation.setMenuId(menuId);
                relation.setCreateUser(operator);
                return relation;
            }).collect(Collectors.toList());
            roleMenuRelationService.saveBatch(list);
        }
    }
}
