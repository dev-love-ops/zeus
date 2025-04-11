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

    public void resolveDeployRecordExample(CicdDeployRecordExample example, CicdDeployForms.DeployRecordQueryForm form){
        // 默认按照创建时间倒序排列
        example.setOrderByClause("create_time desc");
        CicdDeployRecordExample.Criteria criteria = example.createCriteria();

        // 根据构建ID查询
        if (Objects.nonNull(form.getBuildRecordId())){
            criteria.andBuildRecordIdEqualTo(form.getBuildRecordId());
        }
        // 根据appCode查询
        if (Objects.nonNull(form.getAppCode())){
            criteria.andAppCodeEqualTo(form.getAppCode());
        }
        // 根据envCode查询
        if (Objects.nonNull(form.getEnvCode())){
            criteria.andEnvCodeEqualTo(form.getEnvCode());
        }
        // 根据是否为容器部署来查询
        if (Objects.nonNull(form.getDockerDeploy())){
            criteria.andDockerDeployEqualTo(form.getDockerDeploy());
        }
        // 根据发布模式来查询(快速发布/任务模式)
        if (Objects.nonNull(form.getDeployMode())){
            criteria.andDeployModeEqualTo(form.getDeployMode());
        }

        // 根据操作人
        if (StringUtils.isNotBlank(form.getCreateUser())){
            criteria.andCreateUserEqualTo(form.getCreateUser());
        }

        // 是否是回滚
        if (Objects.nonNull(form.getIsRollback())){
            criteria.andRollbackEqualTo(form.getIsRollback());
        }
    }
}
