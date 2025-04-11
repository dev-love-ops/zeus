package com.wufeiqun.zeus.controller.openApi;


import com.alibaba.fastjson2.JSONObject;
import com.wufeiqun.zeus.biz.openApi.OpenApiFacade;
import com.wufeiqun.zeus.biz.openApi.entity.GitlabCallBackDTO;
import com.wufeiqun.zeus.common.entity.CommonVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/openApi/gitlab/webhook")
@RequiredArgsConstructor
public class GitlabWebhookCallbackController {
    private final OpenApiFacade openApiFacade;

    /**
     * gitlab webhook回调地址
     */
    @PostMapping("/")
    public CommonVo<Object> callback(@RequestBody JSONObject json) {
        log.info("GitlabWebhookCallback params={}", json.toString());
        GitlabCallBackDTO dto = JSONObject.parseObject(json.toString(), GitlabCallBackDTO.class);
        openApiFacade.processGitlabWebhook(dto);
        return CommonVo.success(dto);
    }
}
