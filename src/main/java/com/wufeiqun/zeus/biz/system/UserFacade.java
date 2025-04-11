package com.wufeiqun.zeus.biz.system;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wufeiqun.zeus.biz.system.entity.UserForm;
import com.wufeiqun.zeus.biz.system.entity.UserMenuContext;
import com.wufeiqun.zeus.biz.system.entity.UserVO;
import com.wufeiqun.zeus.common.entity.SelectVO;
import com.wufeiqun.zeus.common.utils.DifferenceUtil;
import com.wufeiqun.zeus.dao.Menu;
import com.wufeiqun.zeus.dao.Role;
import com.wufeiqun.zeus.dao.User;
import com.wufeiqun.zeus.dao.UserRoleRelation;
import com.wufeiqun.zeus.service.IMenuService;
import com.wufeiqun.zeus.service.IRoleMenuRelationService;
import com.wufeiqun.zeus.service.IUserRoleRelationService;
import com.wufeiqun.zeus.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.wufeiqun.zeus.common.constant.GlobalConstant.SUPER_ADMIN;

/**
 * @author 吴飞群
 * @createTime 2022/05/17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserFacade {
    private final IUserService userService;
    private final IMenuService menuService;
    private final MenuAdapter menuAdapter;
    private final IUserRoleRelationService userRoleRelationService;
    private final IRoleMenuRelationService roleMenuRelationService;

    /**
     * 用于用户下拉框的用户列表
     */
    public List<SelectVO> getSelectableUserList(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("status", 1)
                .select("account", "username");

        return userService.list(queryWrapper).stream().map(user -> {
            SelectVO vo = new SelectVO();
            vo.setValue(user.getAccount());
            vo.setLabel(user.getUsername());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 用户列表, 管理页面使用
     */
    public IPage<UserVO> getPageableUserList(UserForm.UserSearchForm form){
        Page<User> pageRequest = new Page<>(form.getPageNum(), form.getPageSize());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        if (StringUtils.isNotBlank(form.getAccount())){
            queryWrapper.like("account", form.getAccount());
        }
        if (StringUtils.isNotBlank(form.getUsername())){
            queryWrapper.like("username", form.getUsername());
        }

        IPage<User> userPage = userService.page(pageRequest, queryWrapper);

        IPage<UserVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        voPage.setRecords(
                userPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList())
        );
        return voPage;

    }

    private UserVO convertToVO(User user){
        UserVO vo = new UserVO();
        vo.setAccount(user.getAccount());
        vo.setUsername(user.getUsername());
        vo.setStatus(user.getStatus());

        List<Role> roleList = userService.getUserRoleList(user.getAccount());
        vo.setRoleList(roleList.stream().map(Role::getId).collect(Collectors.toList()));
        vo.setRoleNameList(roleList.stream().map(Role::getName).collect(Collectors.toList()));

        if (user.getStatus()){
            vo.setStatusDesc("启用");
        } else {
            vo.setStatusDesc("禁用");
        }

        return vo;
    }

    public User getUserByAccount(String account){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        return userService.getOne(queryWrapper);
    }

    public boolean validateToken(String account, String password){
        User user = getUserByAccount(account);
        if (Objects.isNull(user)){
            return false;
        }
        return DigestUtil.bcryptCheck(password, user.getPassword());
    }

    /**
     * 修改用户信息, 这里暂时只允许修改用户的角色, 其他信息后续会从企业微信来同步
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUserRoleRelation(UserForm.UpdateUserForm form, String operator){
        QueryWrapper<UserRoleRelation> wrapper = new QueryWrapper<>();
        wrapper.eq("account", form.getAccount());
        // 获取用户当前所拥有的角色编码列表
        List<String> userExistRoleCodeList = userRoleRelationService.list(wrapper).stream()
                .map(UserRoleRelation::getRoleCode).toList();
        // 获取用户最新的用户角色编码列表
        List<String> userNewRoleCodeList = form.getRoleList();
        //本地存在, 最新请求数据不存在 为需要删除的数据
        List<String> needDelete = DifferenceUtil.differenceObject(userExistRoleCodeList, userNewRoleCodeList);
        //更新存在, 本地不存在 为新增数据
        List<String> needInsert = DifferenceUtil.differenceObject(userNewRoleCodeList, userExistRoleCodeList);
        // 删除
        QueryWrapper<UserRoleRelation> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("account", form.getAccount()).in("role_code", needDelete);
        userRoleRelationService.remove(deleteWrapper);
        // 新增
        List<UserRoleRelation> list =  needInsert.stream().map((item)->{
            UserRoleRelation relation = new UserRoleRelation();
            relation.setAccount(form.getAccount());
            relation.setRoleCode(item);
            relation.setCreateUser(operator);
            return relation;
        }).toList();
        userRoleRelationService.saveBatch(list);

    }

    /**
     * 查询用户的权限编码, 用于前端/后端接口的校验
     * TODO: 这里可以加缓存/删除缓存等
     */
    public Set<String> getUserPermissionCodeList(String account) {
        if (SUPER_ADMIN.equals(account)){
            return menuService.list().stream().map(Menu::getCode).collect(Collectors.toSet());
        }
        return userService.getUserPermissionCodeList(account);
    }

    public List<Tree<String>> getUserMenuList(String account){
        // 配置
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        treeNodeConfig.setWeightKey("sort");
        UserMenuContext context = menuAdapter.createUserMenuContext(account);

        //转换器
        List<Tree<String>> ret = TreeUtil.build(context.getMenuList(), "0", treeNodeConfig, (treeNode, tree) -> {
            tree.setId(String.valueOf(treeNode.getId()));
            tree.setParentId(String.valueOf(treeNode.getParentId()));
            tree.setName(treeNode.getCode());
            // 扩展属性
            tree.putExtra("path", treeNode.getPath());
            tree.putExtra("component", treeNode.getComponent());
            if (StringUtils.isNotEmpty(treeNode.getRedirect())){
                tree.putExtra("redirect", treeNode.getRedirect());
            }
            Map<String, Object> meta = new HashMap<>();
            meta.put("title", treeNode.getTabName());
            if (StringUtils.isNotEmpty(treeNode.getIcon())){
                meta.put("icon", treeNode.getIcon());
            }
            if (treeNode.getHide() == 1){
                meta.put("hideMenu", true);
                meta.put("currentActiveMenu", treeNode.getCurrentActiveMenu());
            }
            tree.putExtra("sort", treeNode.getSort());
            tree.put("meta", meta);
        });

        if (Objects.isNull(ret)){
            return Collections.emptyList();
        }

        return ret;
    }

    public void changePassword(UserForm.ChangePasswordForm form, String operator){
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("account", operator);

        User user = new User();
        user.setPassword(DigestUtil.bcrypt(form.getPassword()));

        userService.update(user, updateWrapper);
    }

}
