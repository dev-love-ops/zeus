package com.wufeiqun.zeus.biz.kubernetes.entity;

import io.kubernetes.client.openapi.ApiClient;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

/**
 * @author wufeiqun
 * @date 2022-11-25
 */
public class KubernetesForm {
    @Data
    @ToString
    public static class PodTerminalForm{
        public PodTerminalForm(Map<String, String> params, ApiClient apiClient) {
            this.envCode = params.get("envCode");
            this.namespace = params.get("namespace");
            this.podName = params.get("podName");
            this.columns = "120";
            this.rows = "40";
            this.apiClient = apiClient;
        }

        private String envCode;
        private String namespace;
        private String podName;
        private String columns;
        private String rows;
        private ApiClient apiClient;

    }

    @Data
    @ToString
    public static class DeleteNamespacedPodForm{
        private String name;
        private String namespace;
        private String envCode;
    }

    @Data
    @ToString
    public static class ViewPodLogForm{
        private String name;
        private String namespace;
        private String envCode;

        private Integer logLines = 500;
    }
}
