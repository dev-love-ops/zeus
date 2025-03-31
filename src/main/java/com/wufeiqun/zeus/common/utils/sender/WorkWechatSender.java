package com.wufeiqun.zeus.common.utils.sender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author wufeiqun
 * @date 2022-08-29
 * 企业微信机器人相关操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkWechatSender {
    private final WebClient webClient;


    public void send(String key, String message){
        baseSend(key, message, WorkwxMessageTypeEnum.TEXT.getCode());
    }
    public void sendMarkdown(String key, String message){
        baseSend(key, message, WorkwxMessageTypeEnum.MARKDOWN.getCode());
    }
    private void baseSend(String key, String message, String msgType){
        log.info("WorkWechatSender.send, 参数={}", message);

        WorkWechatForm.RobotMessage form = new WorkWechatForm.RobotMessage();
        form.setMsgtype(msgType);
        if (WorkwxMessageTypeEnum.TEXT.getCode().equals(msgType)){
            form.setText(WorkWechatFormContent.builder().content(message).build());
        } else {
            form.setMarkdown(WorkWechatFormContent.builder().content(message).build());
        }

        String qyWechatBaseUrl = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=";
        String result = webClient.post()
                .uri(qyWechatBaseUrl +key)
                .bodyValue(form).retrieve().bodyToMono(String.class).block();
        log.info("WorkWechatSender.send, 返回={}", result);
    }
}
