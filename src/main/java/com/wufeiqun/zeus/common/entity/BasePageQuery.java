package com.wufeiqun.zeus.common.entity;

import lombok.Data;

/**
 * @author wufeiqun
 * @date 2022-07-07
 */
@Data
public class BasePageQuery {
    private int pageNum = 1;
    private int pageSize = 10;
}
