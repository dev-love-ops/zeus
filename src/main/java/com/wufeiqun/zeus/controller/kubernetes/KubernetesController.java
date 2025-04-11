package com.wufeiqun.zeus.controller.kubernetes;

import com.wufeiqun.zeus.biz.kubernetes.KubernetesFacade;
import com.wufeiqun.zeus.biz.kubernetes.entity.KubernetesForm;
import com.wufeiqun.zeus.common.entity.CommonVo;
import com.wufeiqun.zeus.common.utils.RequestUtil;
import com.wufeiqun.zeus.dao.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wufeiqun
 * @date 2022-07-07
 */
@Slf4j
@RestController
@RequestMapping("/api/kubernetes")
@RequiredArgsConstructor
public class KubernetesController {
    private final KubernetesFacade kubernetesFacade;

    @PostMapping("/deleteNamespacedPod")
    public CommonVo<Boolean> delete(@RequestBody @Valid KubernetesForm.DeleteNamespacedPodForm form){
        User user = RequestUtil.getCurrentUser();
        return CommonVo.success(kubernetesFacade.deleteNamespacedPod(form, user.getAccount()));
    }

    @PostMapping("/viewPodLog")
    public CommonVo<String> viewPodLog(@RequestBody @Valid KubernetesForm.ViewPodLogForm form){
        kubernetesFacade.viewPodLog(form);
        return CommonVo.success("OK");
    }
}
