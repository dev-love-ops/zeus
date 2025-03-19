package com.wufeiqun.zeus.biz.system.entity;

import com.wufeiqun.zeus.common.entity.BasePageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wufeiqun
 * @date 2022-07-08
 */
public class OperationRecordForm {


    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class OperationRecordSearchForm extends BasePageQuery {
        private String query;
    }
}
