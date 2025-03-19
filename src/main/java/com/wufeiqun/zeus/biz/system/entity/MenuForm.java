package com.wufeiqun.zeus.biz.system.entity;

import com.wufeiqun.zeus.dao.Menu;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author wufeiqun
 * @date 2022-09-19
 */
public class MenuForm {
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class UpdateMenuForm extends Menu { }
}
