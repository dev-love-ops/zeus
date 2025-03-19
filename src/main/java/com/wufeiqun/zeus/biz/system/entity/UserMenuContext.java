package com.wufeiqun.zeus.biz.system.entity;

import com.wufeiqun.zeus.dao.Menu;
import com.wufeiqun.zeus.dao.User;
import lombok.Data;

import java.util.List;

/**
 * @author wufeiqun
 * @date 2022-09-02
 */
@Data
public class UserMenuContext {
    private String account;
    private User user;
    private List<Menu> menuList;

    public UserMenuContext(String account){
        this.account = account;
    }
}
