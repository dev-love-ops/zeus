package com.wufeiqun.zeus.biz.openApi;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wufeiqun.zeus.biz.cicd.CicdBuildFacade;
import com.wufeiqun.zeus.biz.cicd.NoticeTemplateMaker;
import com.wufeiqun.zeus.biz.cicd.entity.CicdBuildForm;
import com.wufeiqun.zeus.biz.cicd.enums.ArtifactTypeEnum;
import com.wufeiqun.zeus.biz.cmdb.enums.EnvironmentEnum;
import com.wufeiqun.zeus.biz.openApi.entity.GitlabCallBackDTO;
import com.wufeiqun.zeus.biz.openApi.entity.GitlabCommit;
import com.wufeiqun.zeus.common.utils.sender.WorkWechatSender;
import com.wufeiqun.zeus.dao.ApplicationDeployConfig;
import com.wufeiqun.zeus.service.IApplicationDeployConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wufeiqun
 * @date 2022-09-14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenApiFacade {
    private final CicdBuildFacade cicdBuildFacade;
    private final IApplicationDeployConfigService applicationDeployConfigService;
    private final WorkWechatSender workWechatSender;


    /**
     * 目前git提交即发布功能只适用于下面的场景
     *
     * 1. 打开该功能开关的应用
     * 2. 测试环境
     * 3. jar-api类型的应用, 其他类型暂未测试, 暂时不开放
     */
    public void processGitlabWebhook(GitlabCallBackDTO dto){
        // 只关注push消息
        if (!"push".equals(dto.getObject_kind())){
            log.warn("processGitlabWebhook, 接收到非push类型消息, 忽略");
            return;
        }
        // 获取已经配置 git提交即发布 的应用列表
        QueryWrapper<ApplicationDeployConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("git_ssh_url", dto.getProject().getGit_ssh_url())
                .eq("env_code", EnvironmentEnum.TEST.getCode())
                .eq("auto_deploy_on_git_commit", true);

        List<ApplicationDeployConfig> list =  applicationDeployConfigService.list(queryWrapper);
        if (CollectionUtil.isEmpty(list) || list.size() > 1){
            log.warn("processGitlabWebhook, 未找到匹配的appCode, list={}", JSON.toJSONString(list));
            return;
        }

        ApplicationDeployConfig config = list.getFirst();

        if (!ArtifactTypeEnum.JARAPI.getType().equals(config.getArtifactType())){
            log.warn("processGitlabWebhook, 该功能只适用于jar-api类型应用");
            return;
        }

        if (!config.getBuildBranch().equals(dto.getBranch())){
            log.warn("processGitlabWebhook, 分支不匹配, 忽略, {}", dto);
            return;
        }

        // 执行构建
        CicdBuildForm.RunBuildForm form = new CicdBuildForm.RunBuildForm();
        form.setAppCode(list.getFirst().getAppCode());
        form.setEnvCode(EnvironmentEnum.TEST.getCode());
        form.setBuildBranch(list.getFirst().getBuildBranch());
        cicdBuildFacade.runBuild(form, dto.getUser_username());

        // 执行发布, jar-api类型应用无需发布, 这块后续再实现

        // 通知, 这里只是通知谁提交了和提交的内容, 具体成功失败会有另一个消息
        String workWeixinToken = config.getWorkWeixinToken();
        if (StringUtils.isEmpty(workWeixinToken)){
            log.warn("processGitlabWebhook, 企业微信配置为空, 通知未发送: {}", config.getAppCode());
            return;
        }

        String commits = dto.getCommits().stream().map(GitlabCommit::getUrl).collect(Collectors.joining(", \n"));

        workWechatSender.send(workWeixinToken,
                NoticeTemplateMaker.getAutoDeployOnGitCommitMessage(
                        config.getAppCode(), dto.getProject().getGit_ssh_url(), commits, dto.getUser_username()));
    }
}
