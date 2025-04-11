package com.wufeiqun.zeus.service.impl;

import com.wufeiqun.zeus.common.entity.SelectVO;
import com.wufeiqun.zeus.common.exception.ServiceException;
import com.wufeiqun.zeus.common.utils.sender.WorkWechatSender;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Project;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @author wufeiqun
 * @date 2022-09-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GitlabService {
    @Value("${gitlab.git-addr-prefix}")
    private String gitAddrPrefix;
    @Value("${gitlab.host}")
    private String host;
    @Value("${gitlab.access-token}")
    private String accessToken;
    private final WorkWechatSender workWechatSender;
    private final StringRedisTemplate redisTemplate;


    public List<SelectVO> getBranches(String gitAddress){
        if (!gitAddress.startsWith(gitAddrPrefix)) {
            log.warn("git地址不正确, git={}", gitAddress);
            throw new ServiceException("git地址不正确, 请到<发布配置>中填写正确的git地址!");
        }

        try (GitLabApi gitLabApi = new GitLabApi(host, accessToken)) {
            gitLabApi.setRequestTimeout(5000, 60000);
            Optional<Project> project = getGitLabProjectWithGitAddress(gitLabApi, gitAddress);
            List<Branch> branches = gitLabApi.getRepositoryApi().getBranches(project.get().getId());
            return branches.stream().map( item -> {
                SelectVO vo = new SelectVO();
                vo.setLabel(item.getName());
                vo.setValue(item.getName());
                return vo;
            }
            ).collect(Collectors.toList());
        } catch (GitLabApiException e) {
            log.warn("获取gitlab分之列表失败: ", e);
            return Collections.emptyList();
        }
    }

    public Optional<Project> getGitLabProjectWithGitAddress(GitLabApi gitLabApi, String gitAddress) {
        GitAddrAttribute object = resolveGitAddrAttribute(gitAddress);
        return gitLabApi.getProjectApi().getOptionalProject(object.getNamespace(), object.getProject());
    }

    public GitAddrAttribute resolveGitAddrAttribute(String gitAddress){
        String[] strings = StringUtils.split(gitAddress, ":");
        if (strings.length <= 1) {
            throw new ServiceException("git地址格式异常: " + gitAddress);
        }
        String namespaceProject = strings[strings.length - 1].replace(".git", "");
        String[] results = StringUtils.split(namespaceProject, "/");
        if (results.length <= 1) {
            throw new ServiceException("git地址格式异常: " + gitAddress);
        }

        GitAddrAttribute object = new GitAddrAttribute();

        String namespace = results[0];
        String project = namespaceProject.substring(namespace.length() + 1);
        object.setNamespace(namespace);
        object.setProject(project);

        return object;
    }

    @Data
    public static class GitAddrAttribute {
        private String namespace;
        private String project;
    }
}
