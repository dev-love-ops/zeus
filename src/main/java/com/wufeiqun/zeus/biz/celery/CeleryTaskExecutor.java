package com.wufeiqun.zeus.biz.celery;

import com.alibaba.fastjson2.JSONObject;
import com.wufeiqun.zeus.biz.celery.entity.CeleryCicdBuildForm;
import com.wufeiqun.zeus.biz.celery.entity.CeleryCicdDeployForm;
import com.wufeiqun.zeus.biz.celery.enums.CeleryActionEnum;
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
public class CeleryTaskExecutor {
    private final WebClient webClient;

    @Value("${zeus-task.task-url}")
    private String taskUrl;

    @Autowired
    public CeleryTaskExecutor(WebClient webClient) {
        this.webClient = webClient;
    }

    public boolean doBuild(CeleryCicdBuildForm form){
        log.info("CeleryTaskExecutor.doBuild, 参数={}", form);
        String url = taskUrl + CeleryActionEnum.BUILD.getValue();
        JSONObject result = webClient.post()
                .uri(url)
                .bodyValue(form).retrieve().bodyToMono(JSONObject.class).block();
        log.info("CeleryTaskExecutor.doBuild, 返回={}", result);
        return "0".equals(result.getString("code"));
    }

    public boolean doDeploy(CeleryCicdDeployForm form){
        log.info("CeleryTaskExecutor.doDeploy, 参数={}", form);
        String url = taskUrl + CeleryActionEnum.DEPLOY.getValue();
        JSONObject result = webClient.post()
                .uri(url)
                .bodyValue(form).retrieve().bodyToMono(JSONObject.class).block();
        log.info("CeleryTaskExecutor.doDeploy, 返回={}", result);
        if (!"0".equals(result.getString("code"))){
            return false;
        }
        return true;
    }

    public boolean doRestart(CeleryCicdRestartForm form){
        log.info("CeleryTaskExecutor.doRestart, 参数={}", form);
        String url = taskUrl + CeleryActionEnum.RESTART.getValue();
        try{
            JSONObject result = webClient.post()
                    .uri(url)
                    .bodyValue(form).retrieve().bodyToMono(JSONObject.class).block();
            log.info("CeleryTaskExecutor.doRestart, 返回={}", result);

            if (!"0".equals(result.getString("code"))){
                return false;
            }
            return true;
        } catch (Exception e){
            log.warn("调用异步任务服务doRestart异常, 参数={}", form, e);
            return false;
        }

    }

    public boolean doRollback(CeleryCicdDeployForm form){
        log.info("CeleryTaskExecutor.doRollback, 参数={}", form);
        String url = taskUrl + CeleryActionEnum.ROLLBACK.getValue();
        JSONObject result = webClient.post()
                .uri(url)
                .bodyValue(form).retrieve().bodyToMono(JSONObject.class).block();
        log.info("CeleryTaskExecutor.doRollback, 返回={}", result);
        if (!"0".equals(result.getString("code"))){
            return false;
        }
        return true;
    }
}
