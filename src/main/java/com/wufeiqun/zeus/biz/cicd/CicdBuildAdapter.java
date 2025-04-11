package com.wufeiqun.zeus.biz.cicd;

import cn.hutool.core.bean.BeanUtil;
import com.wufeiqun.zeus.biz.cicd.entity.CicdBuildContext;
import com.wufeiqun.zeus.biz.cicd.entity.CicdBuildRecordVO;
import com.wufeiqun.zeus.biz.cicd.enums.CicdBuildStatusEnum;
import com.wufeiqun.zeus.biz.cmdb.enums.EnvironmentEnum;
import com.wufeiqun.zeus.dao.ApplicationDeployConfig;
import com.wufeiqun.zeus.dao.CicdBuildRecord;
import com.wufeiqun.zeus.dao.User;
import com.wufeiqun.zeus.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wufeiqun
 * @date 2022-08-16
 */
@Service
@RequiredArgsConstructor
public class CicdBuildAdapter {
    private final IUserService userService;

    public CicdBuildRecord convertToCicdBuildRecord(CicdBuildContext context){
        CicdBuildRecord record = new CicdBuildRecord();

        record.setBuildAppCode(context.getRunBuildForm().getAppCode());
        record.setBuildEnvCode(context.getRunBuildForm().getEnvCode());
        record.setDeployMode(context.getRunBuildForm().getDeployMode());
        record.setCreateUser(context.getOperator());
        ApplicationDeployConfig config = context.getApplicationDeployConfig();
        record.setBuildBranch(context.getBuildBranch());
        record.setBuildProfile(config.getProfile());
        record.setBuildTag(context.getBuildTag());
        record.setBuildStatus(CicdBuildStatusEnum.BUILDING.getCode());
        return record;
    }

    public List<CicdBuildRecordVO> transformToBuildRecord(List<CicdBuildRecord> list){
        Map<String, User> userMap =  userService.getAccountUserMap();
        return list.stream().map(item -> {
            CicdBuildRecordVO vo = new CicdBuildRecordVO();
            BeanUtil.copyProperties(item, vo);
            vo.setStatusDesc(CicdBuildStatusEnum.descWithCode(item.getBuildStatus()));
            vo.setBuildEnvCode(EnvironmentEnum.descWithCode(item.getBuildEnvCode()));
            if (userMap.containsKey(item.getCreateUser())){
                vo.setCreateUser(userMap.get(item.getCreateUser()).getUsername());
            }
            return vo;
        }).collect(Collectors.toList());
    }

}
