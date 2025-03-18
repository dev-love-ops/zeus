package com.wufeiqun.zeus.biz.system.entity;

import com.wufeiqun.zeus.dao.Role;
import com.wufeiqun.zeus.dao.User;
import lombok.Data;
import lombok.EqualsAndHashCode;


import java.util.List;
import java.util.Set;

/**
 * @author wufeiqun
 * @date 2022-07-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserInfo extends User {
    private List<Role> roles;
    private String avatar;
    private String homePath;
    private Set<String> permissionCodeList;
}
