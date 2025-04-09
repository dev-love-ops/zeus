package com.wufeiqun.zeus.biz.cicd.entity;

import com.wufeiqun.zeus.dao.CicdDeployRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CicdDeployRecordVO  extends CicdDeployRecord {
    private String statusDesc;
}
