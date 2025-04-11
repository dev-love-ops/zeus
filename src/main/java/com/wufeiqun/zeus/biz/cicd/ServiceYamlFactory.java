package com.wufeiqun.zeus.biz.cicd;

import com.wufeiqun.zeus.biz.cicd.entity.CicdDeployContext;
import com.wufeiqun.zeus.common.exception.ServiceException;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServicePort;
import io.kubernetes.client.openapi.models.V1ServiceSpec;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * kubernetes service生成
 */
@Slf4j
@Service
public class ServiceYamlFactory {
    @Value("classpath:kubernetes/template/service.yaml")
    private Resource serviceYamlTemplateResource;
    private V1Service v1Service;
    private CicdDeployContext deployContext;
    private Integer SERVICE_PORT = 8080;


    public void prepare(CicdDeployContext context) {
        try {
            prepareServiceTemplate();
        } catch (IOException e) {
            log.error("prepareServiceTemplate, 异常", e);
            throw new ServiceException("读取kubernetes service模板异常!");
        }
        this.deployContext = context;
        // 可以在这里处理ENV或者放到构建上下文
    }

    private void prepareServiceTemplate() throws IOException {
        InputStream prodConfigmapTemplateResourceInputStream = serviceYamlTemplateResource.getInputStream();
        String content = IOUtils.toString(prodConfigmapTemplateResourceInputStream, StandardCharsets.UTF_8);
        this.v1Service = Yaml.loadAs(content, V1Service.class);
    }

    public void create() {
        resolveServiceMetadata();
        resolveServiceSpec();
        deployContext.setServiceYaml(Yaml.dump(v1Service));
    }

    private void resolveServiceSpec() {
        V1ServiceSpec spec = v1Service.getSpec();
        if (spec == null) {
            throw new ServiceException("kubernetes service模板信息错误, ");
        }
        spec.putSelectorItem("app", deployContext.getRunDeployForm().getAppCode());
        List<V1ServicePort> v1ServicePortList =  spec.getPorts();
        if (CollectionUtils.isEmpty(v1ServicePortList) || v1ServicePortList.size() != 1){
            throw new ServiceException("kubernetes service模板信息错误, 端口配置有误");
        }
        buildPort(v1ServicePortList.get(0));
    }

    private void resolveServiceMetadata() {
        V1ObjectMeta metadata = v1Service.getMetadata();
        if (metadata == null) {
            throw new ServiceException("生产service模板信息错误");
        }

        metadata.namespace(deployContext.getApplicationDeployConfig().getKubernetesNamespace())
                .name(deployContext.getRunDeployForm().getAppCode())
                .putLabelsItem("app", deployContext.getRunDeployForm().getAppCode());
    }

    private void buildPort(V1ServicePort v1ServicePort) {
        Integer httpPort = deployContext.getApplicationDeployConfig().getPort();
        Integer nodePort = deployContext.getApplicationDeployConfig().getKubernetesNodePort();
        // 集群内部访问service使用, 比如ingress
        v1ServicePort.setPort(SERVICE_PORT);
        v1ServicePort.setTargetPort(new IntOrString(httpPort));
        v1ServicePort.setProtocol("TCP");
        // 每个应用根据范围生成一个唯一的nodePort, 如果没有主动生成就会随机分配一个, 一般web类应用要主动指定
        // consumer类的可以不用关心
        if (Objects.nonNull(nodePort) && !nodePort.equals(0)) {
            v1ServicePort.setNodePort(nodePort);
        }
    }
}
