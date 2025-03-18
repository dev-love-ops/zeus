package com.wufeiqun.zeus.biz.system;


import cn.hutool.crypto.digest.DigestUtil;

import com.wufeiqun.zeus.entity.User;
import com.wufeiqun.zeus.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 吴飞群
 * @createTime 2022/05/17
 */
@Slf4j
@Service
public class UserFacade {

    @Autowired
    public UserFacade(UserService userService) {

    }



}
