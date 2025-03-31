package com.wufeiqun.zeus.biz.cmdb.entity;

import com.wufeiqun.zeus.dao.Application;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicationVO extends Application {
    private String ownerName;
    private String departmentName;
    private Boolean isMyFavorite;
}
