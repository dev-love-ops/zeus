package com.wufeiqun.zeus.biz.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wufeiqun.zeus.biz.system.entity.UserForm;
import com.wufeiqun.zeus.biz.system.entity.UserVO;
import com.wufeiqun.zeus.common.entity.SelectVO;
import com.wufeiqun.zeus.dao.User;
import com.wufeiqun.zeus.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 吴飞群
 * @createTime 2022/05/17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserFacade {
    private final IUserService userService;

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
//        vo.setRoleList(user.getRoleList());
//        vo.setRoleNameList(user.getRoleNameList());

        if (user.getStatus()){
            vo.setStatusDesc("启用");
        } else {
            vo.setStatusDesc("禁用");
        }

        return vo;
    }


}
