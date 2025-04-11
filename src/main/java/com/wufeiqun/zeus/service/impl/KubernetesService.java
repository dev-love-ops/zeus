package com.wufeiqun.zeus.service.impl;


import com.wufeiqun.zeus.biz.cmdb.enums.EnvironmentEnum;
import io.kubernetes.client.PodLogs;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author wufeiqun
 * @date 2022-11-07
 */
@Slf4j
@Service
public class KubernetesService {

    @Resource
    private CoreV1Api coreV1Api;
    @Resource
    private ApiClient apiClient;


    public void deleteNamespacedPod(String envCode, String name, String namespace) throws ApiException {
        coreV1Api.deleteNamespacedPod(name, namespace);
    }

    public void viewPodLog(String envCode, String name, String namespace, Integer logLines) throws ApiException, IOException {
        CoreV1Api.APIreadNamespacedPodLogRequest x = coreV1Api.readNamespacedPodLog(name, namespace);
    }

    public void getPodList(String envCode, String namespace, String appCode) throws ApiException {
        String labelSelector = String.format("app=%s", appCode);
            coreV1Api.listNamespacedPod(namespace);
    }
}
