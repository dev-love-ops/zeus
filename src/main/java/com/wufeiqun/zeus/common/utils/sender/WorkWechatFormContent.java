package com.wufeiqun.zeus.common.utils.sender;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author wufeiqun
 * @date 2022-08-29
 */
@Data
@Builder
public class WorkWechatFormContent {
    private String content;
    /**
     * 群里@群成员, "mentioned_mobile_list":["13800001111","@all"]
     */
    private List<String> mentioned_mobile_list;
}
