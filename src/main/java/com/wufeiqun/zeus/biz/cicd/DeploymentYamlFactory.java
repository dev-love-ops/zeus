package com.wufeiqun.zeus.biz.cicd;

import cn.hutool.core.collection.CollectionUtil;
import com.wufeiqun.zeus.biz.cicd.entity.CicdDeployContext;
import com.wufeiqun.zeus.biz.cicd.enums.ScheduleStrategyEnum;
import com.wufeiqun.zeus.biz.cmdb.enums.EnvironmentEnum;
import com.wufeiqun.zeus.common.exception.ServiceException;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 处理kubernetes deployment Yaml配置文件
 */
@Slf4j
@Service
public class DeploymentYamlFactory {
    @Value("classpath:kubernetes/template/deployment.yaml")
    private Resource deploymentYamlTemplateResource;
    private V1Deployment v1Deployment;
    private Map<String, String> envVariables = new HashMap<>(16);
    private CicdDeployContext deployContext;
    private final String LOG_VOLUME_NAME = "app-logs";

    public void prepare(CicdDeployContext context) {
        try {
            prepareDeploymentTemplate();
        } catch (IOException e) {
            log.error("prepareDeploymentTemplate, 异常", e);
            throw new ServiceException("读取kubernetes deployment模板异常!");
        }
        this.deployContext = context;
        // 可以在这里处理ENV或者放到构建上下文
        envVariables.put("BUILD_RECORD_ID", context.getBuildRecord().getId().toString());
        envVariables.put("DEPLOY_RECORD_ID", context.getDeployRecordId().toString());
    }

    private void prepareDeploymentTemplate() throws IOException {
        InputStream prodConfigmapTemplateResourceInputStream = deploymentYamlTemplateResource.getInputStream();
        String content = IOUtils.toString(prodConfigmapTemplateResourceInputStream, StandardCharsets.UTF_8);
        this.v1Deployment = Yaml.loadAs(content, V1Deployment.class);
    }

    public void create() {
        resolveDeploymentMetadata();
        resolveDeploymentSpec();
        deployContext.setDeploymentYaml(Yaml.dump(v1Deployment));
    }

    private void resolveDeploymentSpec() {
        V1DeploymentSpec spec = v1Deployment.getSpec();
        if (spec == null) {
            throw new ServiceException("kubernetes deployment模板信息错误 resolveDeploymentSpec");
        }
        spec.setReplicas(deployContext.getApplicationDeployConfig().getKubernetesReplicas());
        // 这个是使用kubectl rollout undo 回滚的时候使用的, 默认是存了10个rs记录, 我们采用的运维平台的回滚动作, 没有使用默认的
        // 所以这里就只象征性保留1个, 减少无用空间的占用
        spec.setRevisionHistoryLimit(1);
        resolveDeploymentSpecSelector(spec);
        resolveDeploymentSpecTemplate(spec);
    }

    private void resolveDeploymentSpecTemplate(V1DeploymentSpec spec) {
        V1PodTemplateSpec specTemplate = spec.getTemplate();

        resolveDeploymentSpecTemplateMetadata(specTemplate);
        resolveDeploymentSpecTemplateSpec(specTemplate);
    }

    private void resolveDeploymentSpecTemplateSpec(V1PodTemplateSpec specTemplate) {
        V1PodSpec spec = specTemplate.getSpec();
        if (spec == null) {
            throw new ServiceException("kubernetes deployment 模板信息错误 resolveDeploymentSpecTemplateSpec");
        }
        // 存储定义
        spec.addVolumesItem(buildLogVolume());
        // 反亲和性, 同一个应用尽量调度到不同的服务器上, 防止某一个机器挂了影响服务
        spec.setAffinity(buildAffinity());

        List<V1Container> containers = spec.getContainers();
        if (containers.size() != 1) {
            throw new ServiceException("kubernetes deployment 模板信息错误 resolveDeploymentSpecTemplateSpec");
        }
        // 优雅关闭等待时间, 关闭POD的时候先发送SIGTERM信号, 最多等待TerminationGracePeriodSeconds以后如果还没关闭就会发送SIGKILL信号
        // 默认是30秒, 这里设置了10秒
        spec.setTerminationGracePeriodSeconds(10L);
        spec.shareProcessNamespace(true);
        // 异常退出才会重启, spec.template.spec.restartPolicy: Unsupported value: "OnFailure": supported values: "Always"
//        spec.setRestartPolicy("OnFailure");
        // DNS策略配置
        resolvePodDnsConfig(spec);
        resolveMainContainer(containers.get(0));
    }

    /**
     * DNS Policy参考:
     * <a href="https://help.aliyun.com/document_detail/188179.htm">DNS最佳实践</a>
     * <a href="https://help.aliyun.com/document_detail/205713.htm">LocalDNSCache使用</a>
     */
    private void resolvePodDnsConfig(V1PodSpec spec){
        // dnsPolicy：必须为None。表示使用自定义的DNSConfig
        spec.setDnsPolicy("None");

        V1PodDNSConfig dnsConfig = new V1PodDNSConfig();
        // nameservers：配置成169.254.20.10和kube-dns的ClusterIP对应的IP地址。不同环境的ClusterIP不一样
        if (EnvironmentEnum.PROD.getCode().equals(deployContext.getRunDeployForm().getEnvCode()) || EnvironmentEnum.PRE.getCode().equals(deployContext.getRunDeployForm().getEnvCode())){
            dnsConfig.setNameservers(Arrays.asList("169.254.20.10","172.23.0.10"));
        } else {
            dnsConfig.setNameservers(Arrays.asList("169.254.20.10","172.21.0.10"));
        }

        V1PodDNSConfigOption option1 = new V1PodDNSConfigOption();
        option1.setName("ndots");
        option1.setValue("3");
        V1PodDNSConfigOption option2 = new V1PodDNSConfigOption();
        option2.setName("attempts");
        option2.setValue("2");
        V1PodDNSConfigOption option3 = new V1PodDNSConfigOption();
        option3.setName("timeout");
        option3.setValue("1");

        dnsConfig.setOptions(Arrays.asList(option1, option2, option3));

        dnsConfig.setSearches(Arrays.asList(String.format("%s.svc.cluster.local",
                        deployContext.getApplicationDeployConfig().getKubernetesNamespace()),
                "svc.cluster.local", "cluster.local"));

        spec.setDnsConfig(dnsConfig);
    }

    private void resolveMainContainer(V1Container mainContainer) {
        mainContainer.name(deployContext.getRunDeployForm().getAppCode())
                .image(deployContext.getBuildRecord().getBuildImageUrl())
                .addVolumeMountsItem(buildLogVolumeMount());

        mainContainer.setTty(true);
        mainContainer.setStdin(true);

        resolveMainContainerResources(mainContainer);
        resolveMainContainerReadinessProbe(mainContainer);
        resolveMainContainerEnv(mainContainer);
//        resolveMainContainerSecurity(mainContainer);
    }

    private void resolveMainContainerEnv(V1Container mainContainer) {
        if (CollectionUtil.isEmpty(envVariables)){return;}
        for (Map.Entry<String, String> envVariable : envVariables.entrySet()) {
            V1EnvVar envVar = new V1EnvVar();
            envVar.setName(envVariable.getKey());
            envVar.setValue(envVariable.getValue());
            mainContainer.addEnvItem(envVar);
        }

    }

    /**
     * 在尝试使用非root用户启动POD的时候遇到一个问题, 磁盘挂载应该是在后面, 使用的root用户
     * 导致dockerfile中的chown没生效, 导致启动的时候老是报写日志权限不足, 后期研究通了再配置吧
     */
    private void resolveMainContainerSecurity(V1Container container) {
        V1SecurityContext securityContext = new V1SecurityContext();
        // 校验POD必须运行在非root用户
        securityContext.setRunAsNonRoot(true);
        // 禁止容器内子进程拥有提升权限的能力，可以降低被容器中的恶意进程实现越权操作的风险。
        securityContext.setAllowPrivilegeEscalation(false);
        // 暂时加上以后会导致没办法写磁盘, 后续测试通过后再配置
        securityContext.setReadOnlyRootFilesystem(true);
        container.securityContext(securityContext);
    }

    private void resolveMainContainerReadinessProbe(V1Container mainContainer) {

        String probeType = deployContext.getApplicationDeployConfig().getProbeType();

        switch (probeType) {

            case "HTTP": {
                V1Probe livenessProbe = buildHttpReadinessProbe();
                V1Probe readinessProbe = buildHttpReadinessProbe();

                mainContainer.setReadinessProbe(readinessProbe);
                mainContainer.setLivenessProbe(livenessProbe);
                break;
            }
            case "TCP": {
                V1Probe livenessProbe = buildTcpReadinessProbe();
                V1Probe readinessProbe = buildTcpReadinessProbe();

                mainContainer.setReadinessProbe(readinessProbe);
                mainContainer.setLivenessProbe(livenessProbe);
                break;
            }
            default: {
                break;
            }
        }

    }

    /**
     * 2022-12-30
     * 经过测试, liveness和readiness在程序运行的整个过程中会持续执行, 也就是会持续调用探活接口
     * 这里要注意频率的配置, 不要太高了, 不然对服务会造成影响
     * https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/
     */
    private V1Probe buildV1ProbeBuilder() {
        V1Probe v1Probe = new V1Probe();
        // 容器启动后要等待多少秒后才启动启动、存活和就绪探针
        v1Probe.setInitialDelaySeconds(deployContext.getApplicationDeployConfig().getInitialDelaySeconds());
        // 连续30次探活失败判定整个探活为失败, 也就是用户最多等待 初始delay时间 + 30 * 10 = 300秒(5分钟)
        // 判定失败以后kubelet会强行杀死该POD并重新启动
        v1Probe.setFailureThreshold(30);
        // 每次探测的超时时间, 默认是1秒, 这里设置为5秒
        v1Probe.setTimeoutSeconds(5);
        // 执行探测的时间间隔
        v1Probe.setPeriodSeconds(10);
        // 连续探测成功几次算成功, 默认探活成功一次就算成功
        v1Probe.setSuccessThreshold(1);

        return v1Probe;
    }

    private V1Probe buildTcpReadinessProbe() {
        Integer tcpPort = deployContext.getApplicationDeployConfig().getPort();

        V1TCPSocketAction tcpSocket = new V1TCPSocketAction();
        tcpSocket.setPort(new IntOrString(tcpPort));

        V1Probe v1Probe = buildV1ProbeBuilder();
        v1Probe.setTcpSocket(tcpSocket);

        return v1Probe;
    }

    private V1Probe buildHttpReadinessProbe() {
        String healthCheckUrl = deployContext.getApplicationDeployConfig().getHealthCheckUri();
        Integer httpPort = deployContext.getApplicationDeployConfig().getPort();

        V1HTTPGetAction httpGet = new V1HTTPGetAction();
        httpGet.setScheme("HTTP");
        httpGet.setPort(new IntOrString(httpPort));
        httpGet.setPath(healthCheckUrl);

        V1Probe v1Probe = buildV1ProbeBuilder();
        v1Probe.setHttpGet(httpGet);

        return v1Probe;
    }


    private void resolveMainContainerResources(V1Container mainContainer) {
        V1ResourceRequirements resourceRequirements = mainContainer.getResources();

        if (resourceRequirements == null) {
            resourceRequirements = new V1ResourceRequirements();
        }

        Map<String, Quantity> limits = new HashMap<>();
        limits.put("memory", Quantity.fromString(deployContext.getApplicationDeployConfig().getKubernetesLimitMemory() + "Mi"));
        limits.put("cpu", Quantity.fromString(deployContext.getApplicationDeployConfig().getKubernetesLimitCpu().toString()));
        // 容器的资源限制这里, 暂时不设置request, 这样可以尽可能地提高资源的使用率, 减少占着茅坑不拉屎的问题, 然后通过监控总的资源的使用情况来确定
        // 是否增加机器还是减少机器
        Map<String, Quantity> requests = new HashMap<>();
        requests.put("memory", Quantity.fromString( (deployContext.getApplicationDeployConfig().getKubernetesLimitMemory() - 500) + "Mi"));
        requests.put("cpu", Quantity.fromString("0.05"));
        resourceRequirements.setRequests(requests);
        resourceRequirements.setLimits(limits);
        mainContainer.setResources(resourceRequirements);
    }

    private V1VolumeMount buildLogVolumeMount() {
        V1VolumeMount v1VolumeMount = new V1VolumeMount();
        v1VolumeMount.setName(LOG_VOLUME_NAME);
        v1VolumeMount.setMountPath(String.format("/cicd/logs/%s", deployContext.getRunDeployForm().getAppCode()));
        return v1VolumeMount;
    }

    private V1Volume buildLogVolume() {
        String logPath = String.format("/cicd/logs/%s-%s",
                deployContext.getApplicationDeployConfig().getKubernetesNamespace(),
                deployContext.getRunDeployForm().getAppCode());

        V1HostPathVolumeSource hostPathVolumeSource = new V1HostPathVolumeSource();
        hostPathVolumeSource.setPath(logPath);
        hostPathVolumeSource.setType("DirectoryOrCreate");
        V1Volume buildable = new V1Volume();
        buildable.setHostPath(hostPathVolumeSource);
        buildable.setName(LOG_VOLUME_NAME);
        return buildable;
    }

    /**
     * 构建亲和性相关对象
     */
    private V1Affinity buildAffinity() {
        V1Affinity v1Affinity = new V1Affinity();
        /* ***************POD反亲和性******************** */
        v1Affinity.setPodAntiAffinity(buildPodAffinity());
        /* ***************NODE亲和性******************** */
        // 目前节点选择的场景就遇到了爬虫团队使用爬虫团队的服务器节点, 其它的使用默认的节点, 暂时没有其它的诉求
        // 等这块诉求变多了的话, 可以把这块变成一个可配置的功能
        // 爬虫目前没有大批量迁移到容器中, 并且及时迁移到容器中, 也有资源使用限制, 对前台业务影响有限, 暂时用不到资源亲和性来分开
        // 后期使用到了以后再加上, 这样就不用给node节点加标签了
        v1Affinity.setNodeAffinity(buildNodeAffinity());
        return v1Affinity;
    }

    private V1PodAntiAffinity buildPodAffinity(){
        V1PodAntiAffinity v1PodAntiAffinity = new V1PodAntiAffinity();
        // 尽量调度到不同的节点, 不是强制的
        V1WeightedPodAffinityTerm v1WeightedPodAffinityTerm = new V1WeightedPodAffinityTerm();

        V1PodAffinityTerm podAffinityTerm = new V1PodAffinityTerm();
        // 这里设置的是node节点的label, 也是k8s的知名常用label
        podAffinityTerm.setTopologyKey("kubernetes.io/hostname");

        V1LabelSelector labelSelector = new V1LabelSelector();
        Map<String, String> labels = new HashMap<>();
        labels.put("app", deployContext.getRunDeployForm().getAppCode());
        labelSelector.setMatchLabels(labels);
        podAffinityTerm.setLabelSelector(labelSelector);

        v1WeightedPodAffinityTerm.setWeight(1);
        v1WeightedPodAffinityTerm.setPodAffinityTerm(podAffinityTerm);
        v1PodAntiAffinity.setPreferredDuringSchedulingIgnoredDuringExecution(Collections.singletonList(v1WeightedPodAffinityTerm));

        return v1PodAntiAffinity;
    }

    /**
     * 使用方法参考 <a href="https://kubernetes.io/zh-cn/docs/concepts/scheduling-eviction/assign-pod-node/">...</a>
     *      <a href="http://zeus-docs.drugcube.com/#/kubernetes/node-label-usage">...</a>
     *
     *  策略内容参考ScheduleStrategyEnum说明
     */
    private V1NodeAffinity buildNodeAffinity(){
        V1NodeAffinity v1NodeAffinity = new V1NodeAffinity();

        V1PreferredSchedulingTerm v1PreferredSchedulingTerm = new V1PreferredSchedulingTerm();
        v1PreferredSchedulingTerm.setWeight(2);

        V1NodeSelectorTerm v1NodeSelectorTerm = new V1NodeSelectorTerm();

        // 前台核心业务不要调度到156这个节点
        if (EnvironmentEnum.PROD.getCode().equals(deployContext.getRunDeployForm().getEnvCode()) && ScheduleStrategyEnum.ONLINE.getCode().equals(deployContext.getApplicationDeployConfig().getKubernetesScheduleStrategy())){
            V1NodeSelectorRequirement requirement1 = new V1NodeSelectorRequirement();
            requirement1.setKey("kubernetes.io/hostname");
            requirement1.setOperator("NotIn");
            requirement1.setValues(Collections.singletonList("k8s-worker-prod172.17.135.156"));
            v1NodeSelectorTerm.setMatchExpressions(Collections.singletonList(requirement1));
        }

        v1PreferredSchedulingTerm.setPreference(v1NodeSelectorTerm);

        v1NodeAffinity.setPreferredDuringSchedulingIgnoredDuringExecution(Collections.singletonList(v1PreferredSchedulingTerm));

        return v1NodeAffinity;
    }

    private void resolveDeploymentSpecTemplateMetadata(V1PodTemplateSpec specTemplate) {
        V1ObjectMeta specMetadata = specTemplate.getMetadata();
        if (specMetadata == null) {
            specMetadata = new V1ObjectMeta().creationTimestamp(null);
        }
        // 给Pod添加label
        specMetadata.putLabelsItem("app", deployContext.getRunDeployForm().getAppCode());
        // 给Pod添加annotation
        Map<String, String> annotations = new HashMap<>();
        // 监控相关的annotation
        if (deployContext.getApplicationDeployConfig().getPrometheusScrape()){
            annotations.put("prometheus.io/scrape", "true");
            annotations.put("prometheus.io/port", deployContext.getApplicationDeployConfig().getPort().toString());
            annotations.put("prometheus.io/path", deployContext.getApplicationDeployConfig().getPrometheusPath());
        } else {
            annotations.put("prometheus.io/scrape", "false");
        }
        // 构建发布相关annotation
        annotations.put("pharmcube.com/buildRecordId", deployContext.getBuildRecord().getId().toString());
        annotations.put("pharmcube.com/deployRecordId", deployContext.getDeployRecordId().toString());

        specMetadata.setAnnotations(annotations);
    }

    private void resolveDeploymentSpecSelector(V1DeploymentSpec spec) {
        spec.getSelector().putMatchLabelsItem("app", deployContext.getRunDeployForm().getAppCode());
    }

    private void resolveDeploymentMetadata() {
        V1ObjectMeta metadata = v1Deployment.getMetadata();
        if (metadata == null) {
            throw new ServiceException("kubernetes deployment yaml模板信息错误 resolveDeploymentMetadata");
        }

        metadata.namespace(deployContext.getApplicationDeployConfig().getKubernetesNamespace())
                .name(deployContext.getRunDeployForm().getAppCode())
                .putLabelsItem("app", deployContext.getRunDeployForm().getAppCode());
    }
}
