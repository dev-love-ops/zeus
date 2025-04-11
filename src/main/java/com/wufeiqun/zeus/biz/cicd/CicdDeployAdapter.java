package com.wufeiqun.zeus.biz.cicd;

import cn.hutool.core.bean.BeanUtil;
import com.wufeiqun.zeus.biz.cicd.entity.CicdDeployContext;
import com.wufeiqun.zeus.biz.cicd.entity.CicdDeployRecordVO;
import com.wufeiqun.zeus.biz.cicd.enums.CicdDeployStatusEnum;
import com.wufeiqun.zeus.biz.cicd.enums.CicdRollbackStatusEnum;
import com.wufeiqun.zeus.biz.cmdb.enums.EnvironmentEnum;
import com.wufeiqun.zeus.dao.CicdDeployRecord;
import com.wufeiqun.zeus.dao.User;
import com.wufeiqun.zeus.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wufeiqun
 * @date 2022-08-16
 */
@Service
@RequiredArgsConstructor
public class CicdDeployAdapter {
    private final IUserService userService;

    public CicdDeployRecord convertToCicdDeployRecord(CicdDeployContext context){
        CicdDeployRecord record = new CicdDeployRecord();

        record.setBuildRecordId(context.getBuildRecord().getId());
        record.setAppCode(context.getRunDeployForm().getAppCode());
        record.setEnvCode(context.getRunDeployForm().getEnvCode());
        record.setDeployMode(context.getRunDeployForm().getDeployMode());
        record.setDockerDeploy(context.getRunDeployForm().getDockerDeploy());
        record.setServerIp(context.getRunDeployForm().getServerIp());
        record.setRollback(context.getRunDeployForm().getIsRollback());
        record.setCreateUser(context.getOperator());

        return record;
    }

    public CicdDeployRecordVO transformToDeployRecord(CicdDeployRecord record){
        Map<String, User> userMap =  userService.getAccountUserMap();
        CicdDeployRecordVO vo = new CicdDeployRecordVO();
        BeanUtil.copyProperties(record, vo);
        if (record.getRollback()){
            vo.setStatusDesc(CicdRollbackStatusEnum.descWithCode(record.getDeployStatus()));
        } else {
            vo.setStatusDesc(CicdDeployStatusEnum.descWithCode(record.getDeployStatus()));
        }

        vo.setEnvCode(EnvironmentEnum.descWithCode(record.getEnvCode()));
        if (userMap.containsKey(record.getCreateUser())){
            vo.setCreateUser(userMap.get(record.getCreateUser()).getUsername());
        }
        return vo;
    }

    public List<CicdDeployRecordVO> transformToDeployRecordList(List<CicdDeployRecord> recordList){
        return recordList.stream().map(this::transformToDeployRecord).collect(Collectors.toList());
    }

}
