package com.wufeiqun.zeus.biz.kubernetes;

import com.wufeiqun.zeus.biz.kubernetes.entity.KubernetesForm;
import com.wufeiqun.zeus.service.impl.KubernetesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wufeiqun
 * @date 2022-11-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KubernetesFacade {
    private final KubernetesService kubernetesService;

    public boolean deleteNamespacedPod(KubernetesForm.DeleteNamespacedPodForm form, String account){
        log.info("用户[{}] deleteNamespacedPod, 参数={}", account, form);

        try{
            kubernetesService.deleteNamespacedPod(form.getEnvCode(), form.getName(), form.getNamespace());
            return true;
        } catch (Exception e){
            log.warn("deleteNamespacedPod 异常, 操作用户={}, 参数={}", account, form, e);
            return false;
        }
    }

    public void viewPodLog(KubernetesForm.ViewPodLogForm form){
        try{
            kubernetesService.viewPodLog(form.getEnvCode(), form.getName(), form.getNamespace(), form.getLogLines());
        } catch (Exception e){
            log.warn("viewPodLog 异常, 参数={}", form, e);
        }
    }
}
