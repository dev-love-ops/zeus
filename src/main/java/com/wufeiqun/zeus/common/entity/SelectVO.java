package com.wufeiqun.zeus.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wufeiqun
 * @date 2022-07-11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectVO {
    private String label;
    private Object value;
}
