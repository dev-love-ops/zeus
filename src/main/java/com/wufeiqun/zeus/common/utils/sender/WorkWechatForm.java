package com.wufeiqun.zeus.common.utils.sender;

import lombok.Data;

/**
 * @author wufeiqun
 * @date 2022-08-29
 */

public class WorkWechatForm {

    @Data
    public static class RobotMessage{
        private String msgtype;
        private WorkWechatFormContent text;
        private WorkWechatFormContent markdown;
    }

}
