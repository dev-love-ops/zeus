package com.wufeiqun.zeus.biz.cicd;

import cn.hutool.core.date.DateUtil;
import com.wufeiqun.zeus.biz.cicd.enums.CicdBuildStatusEnum;
import com.wufeiqun.zeus.biz.cicd.enums.CicdDeployStatusEnum;
import com.wufeiqun.zeus.biz.cmdb.enums.EnvironmentEnum;
import com.wufeiqun.zeus.dao.CicdBuildRecord;
import com.wufeiqun.zeus.dao.CicdDeployRecord;
import com.wufeiqun.zeus.dao.User;
import com.wufeiqun.zeus.service.impl.GitlabService;

import java.text.MessageFormat;
import java.util.Map;

/**
 * @author wufeiqun
 * @date 2022-08-29
 */
public class NoticeTemplateMaker {
    /**
     * @param record 构建记录
     * @param gitAddrAttribute git地址相关属性
     * @param userMap 用户账号和姓名的对应map
     */
    public static String getBuildMessage(CicdBuildRecord record, GitlabService.GitAddrAttribute gitAddrAttribute, Map<String, User> userMap){

        return MessageFormat.format("构建通知 \n构建ID: {0}\nappCode: {1}\n操作人: {2}\n构建时间: {3}\n构建分支: {4}\n构建环境: {5}\n构建状态: {6}\n构建耗时: {7}秒",
                String.valueOf(record.getId()), record.getBuildAppCode(), userMap.get(record.getCreateUser()).getUsername(), DateUtil.formatLocalDateTime(record.getCreateTime()),
                record.getBuildBranch(), EnvironmentEnum.descWithCode(record.getBuildEnvCode()),
                CicdBuildStatusEnum.descWithCode(record.getBuildStatus()), record.getDuration());

    }

    public static String getDeployMessage(CicdDeployRecord record, CicdBuildRecord buildRecord, Map<String, User> userMap){

        return MessageFormat.format("发布通知 \nappCode: {0}\n机器IP: {1}\n操作人: {2}\n构建ID: {3, number, #}\n发布ID: {6}\n发布状态: {4}\n发布时间: {5}",
                record.getAppCode(), record.getServerIp(), userMap.get(record.getCreateUser()).getUsername(), record.getBuildRecordId(),
                CicdDeployStatusEnum.descWithCode(record.getDeployStatus()), DateUtil.formatLocalDateTime(record.getCreateTime()),
                String.valueOf(record.getId()));

    }

    public static String getRollbackMessage(CicdDeployRecord record, Map<String, User> userMap){
        return MessageFormat.format("回滚通知 \nappCode: {0}\n机器IP: {1}\n操作人: {2}\n构建ID: {3, number, #}\n回滚ID: {6}\n回滚状态: {4}\n回滚时间: {5}",
                record.getAppCode(), record.getServerIp(), userMap.get(record.getCreateUser()).getUsername(), record.getBuildRecordId(),
                CicdDeployStatusEnum.descWithCode(record.getDeployStatus()), DateUtil.formatLocalDateTime(record.getCreateTime()),
                String.valueOf(record.getId()));
    }

    public static String getAutoDeployOnGitCommitMessage(String appCode, String gitSshUrl, String commits, String operator){
        return MessageFormat.format("git提交自动发布通知 \nappCode: {0}\n操作人: {1}\ngit仓库: {2}\n提交记录: {3}",
                appCode, operator, gitSshUrl, commits);
    }

}
