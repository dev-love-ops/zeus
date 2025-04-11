package com.wufeiqun.zeus.biz.openApi.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author wufeiqun
 * @date 2022-09-14
 */
@Data
@ToString
public class GitlabCallBackDTO {
    /**
     * 有push/merge_request等, 我们只需要关注push即可监听到直接向某一个分支推送和通过merge向某一个分支推送,
     * merge的时候也会有一个push的消息
     */
    private String object_kind;
    /**
     * 邮箱前缀. 比如`wufeiqun`
     */
    private String user_username;
    /**
     * 中文名, 比如`吴飞群`
     */
    private String user_name;
    /**
     * 分支, 比如`refs/heads/master`
     */
    private String ref;

    private GitLabProject project;
    private List<GitlabCommit> commits;

    public String getBranch(){
        return ref.split("/")[2];
    }
}
