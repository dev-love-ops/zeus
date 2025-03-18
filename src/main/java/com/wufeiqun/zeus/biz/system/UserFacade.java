package com.wufeiqun.zeus.biz.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wufeiqun.zeus.common.entity.SelectVO;
import com.wufeiqun.zeus.dao.User;
import com.wufeiqun.zeus.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


}
