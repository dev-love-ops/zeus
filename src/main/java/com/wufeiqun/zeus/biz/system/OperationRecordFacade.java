package com.wufeiqun.zeus.biz.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wufeiqun.zeus.biz.system.entity.OperationRecordForm;
import com.wufeiqun.zeus.biz.system.enums.OperationTypeEnum;
import com.wufeiqun.zeus.dao.OperationRecord;
import com.wufeiqun.zeus.dao.User;
import com.wufeiqun.zeus.service.IOperationRecordService;
import com.wufeiqun.zeus.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationRecordFacade {
    private final IUserService userService;
    private final IOperationRecordService operationRecordService;

    public IPage<OperationRecord> getPageableOperationRecordList(OperationRecordForm.OperationRecordSearchForm form){
        Page<OperationRecord> pageRequest = new Page<>(form.getPageNum(), form.getPageSize());

        QueryWrapper<OperationRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        if (StringUtils.isNotBlank(form.getQuery())){
            queryWrapper.like("content", form.getQuery());
        }

        IPage<OperationRecord> operationRecordPage = operationRecordService.page(pageRequest, queryWrapper);

        IPage<OperationRecord> voPage = new Page<>(operationRecordPage.getCurrent(), operationRecordPage.getSize(),
                operationRecordPage.getTotal());

        voPage.setRecords(convertToVO(operationRecordPage.getRecords()));

        return voPage;

    }

    private List<OperationRecord> convertToVO(List<OperationRecord> list){
        Map<String, User> userMap = userService.getAccountUserMap();
        for (OperationRecord item : list) {
            // 处理操作人中文显示
            if (userMap.containsKey(item.getCreateUser())){
                item.setCreateUser(userMap.get(item.getCreateUser()).getUsername());
            }
            // 处理操作类型中文显示
            item.setType(OperationTypeEnum.descWithCode(item.getType()));
        }

        return list;
    }
}
