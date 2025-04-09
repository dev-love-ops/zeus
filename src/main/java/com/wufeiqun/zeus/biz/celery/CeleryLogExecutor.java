package com.wufeiqun.zeus.biz.celery;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.wufeiqun.zeus.biz.celery.entity.ReadLogForm;
import com.wufeiqun.zeus.biz.celery.entity.ReadLogVO;
import com.wufeiqun.zeus.biz.celery.enums.CeleryActionEnum;
import com.wufeiqun.zeus.common.entity.CommonVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author wufeiqun
 * @date 2022-08-18
 */
@Slf4j
@Service
public class CeleryLogExecutor {
    @Value("${zeus-task.read-log-url}")
    private String readLogUrl;

    private final WebClient webClient;

    @Autowired
    public CeleryLogExecutor(WebClient webClient) {
        this.webClient = webClient;
    }

    private ReadLogVO doReadLog(ReadLogForm form) {
        ReadLogVO readLogVo = new ReadLogVO();
        readLogVo.setLogContent("");
        readLogVo.setOffset(form.getOffset());

        try {
            log.info("CeleryLogExecutor.doReadLog, 参数={}", form);
            String result = webClient.post()
                    .uri(readLogUrl)
                    .bodyValue(form).retrieve().bodyToMono(String.class).block();
            log.info("CeleryLogExecutor.doReadLog, 返回={}", result);

            CommonVo<ReadLogVO> resultVo = JSON.parseObject(result, new TypeReference<CommonVo<ReadLogVO>>() {
            });

            if (!resultVo.isSuccess()) {
                log.error("readBuildLog error");
                return readLogVo;
            }
            ReadLogVO data = resultVo.getData();

            readLogVo.setOffset(data.getOffset());
            readLogVo.setLogContent(data.getLogContent());
            readLogVo.setCompleteFlag(data.isCompleteFlag());

        } catch (Exception e) {
            log.error("readBuildLog error", e);
        }

        return readLogVo;

    }

    public ReadLogVO readBuildLog(ReadLogForm form) {
        form.setAction(CeleryActionEnum.BUILD.getValue());
        return doReadLog(form);
    }

    public ReadLogVO readDeployLog(ReadLogForm form) {
        form.setAction(CeleryActionEnum.DEPLOY.getValue());
        return doReadLog(form);
    }

    public ReadLogVO readRollbackLog(ReadLogForm form) {
        form.setAction(CeleryActionEnum.ROLLBACK.getValue());
        return doReadLog(form);
    }

    public ReadLogVO readRestartLog(ReadLogForm form) {
        form.setAction(CeleryActionEnum.RESTART.getValue());
        return doReadLog(form);
    }

}
