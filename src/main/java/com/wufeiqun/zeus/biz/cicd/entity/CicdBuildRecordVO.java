package com.wufeiqun.zeus.biz.cicd.entity;

import com.wufeiqun.zeus.dao.CicdBuildRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CicdBuildRecordVO extends CicdBuildRecord {
    private String statusDesc;
}
