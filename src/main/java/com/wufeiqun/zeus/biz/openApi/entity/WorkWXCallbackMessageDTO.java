package com.wufeiqun.zeus.biz.openApi.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @author wufeiqun
 * @date 2023-07-03
 */
@Data
@ToString
public class WorkWXCallbackMessageDTO {
    /**
     * 文本消息内容,最长不超过2048个字节，超过将截断
     */
    private String Content;
    /**
     * 消息类型，用户跟应用对话的普通文本回调消息为：text
     */
    private String MsgType;
    /**
     * 对话用户的企业微信ID
     */
    private String FromUserName;
    /**
     * 消息的唯一ID, 一般用不到
     */
    private String MsgId;
}
