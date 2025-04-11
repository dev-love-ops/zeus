package com.wufeiqun.zeus.common.config;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author wufeiqun
 * @date 2022-11-07
 */
@Slf4j
@Configuration()
public class KubernetesConfig {
    @Value("classpath:k8s/kube_config")
    private Resource kubeConfig;


    private ApiClient createApiClient(InputStream kubeConfig) throws IOException {
        return Config.fromConfig(kubeConfig);
//        return new ClientBuilder().setBasePath(basePath).setVerifyingSsl(false)
//                .setAuthentication(new AccessTokenAuthentication(token))
//                .setReadTimeout(Duration.ofSeconds(30)).build();
    }

    private CoreV1Api createCoreV1Api(InputStream kubeConfig) throws IOException {
//        ApiClient client = new ClientBuilder().setBasePath(basePath).setVerifyingSsl(false)
//                .setAuthentication(new AccessTokenAuthentication(token))
//                .setReadTimeout(Duration.ofSeconds(30)).build();
        ApiClient client = Config.fromConfig(kubeConfig);
        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(client);
        return new CoreV1Api();
    }

    @Bean
    public CoreV1Api createProdCoreV1Api() throws IOException {
        log.info("kubernetes CoreV1Api created!");
        return createCoreV1Api(kubeConfig.getInputStream());
    }

    @Bean
    public ApiClient createProdApiClient() throws IOException {
        log.info("kubernetes ApiClient created!");
        return createApiClient(kubeConfig.getInputStream());
    }






}
