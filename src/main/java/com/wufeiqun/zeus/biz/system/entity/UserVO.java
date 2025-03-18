package com.wufeiqun.zeus.biz.system.entity;

import lombok.Data;

import java.util.List;

@Data
public class UserVO {
    private String account;
    private String username;
    private Boolean status;
    private List<Long> roleList;
    private List<String> roleNameList;
    private String statusDesc;
}
