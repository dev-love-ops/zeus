package com.wufeiqun.zeus.controller.system;

import com.wufeiqun.zeus.biz.system.OperationRecordFacade;
import com.wufeiqun.zeus.biz.system.entity.OperationRecordForm;
import com.wufeiqun.zeus.common.entity.CommonVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/system/operationRecord/")
@RequiredArgsConstructor
public class OperationRecordController {

    private final OperationRecordFacade operationRecordFacade;


    @PostMapping("/getPageableOperationRecordList")
    public CommonVo<Object> getPageableOperationRecordList(@RequestBody OperationRecordForm.OperationRecordSearchForm form) {
        return CommonVo.success(operationRecordFacade.getPageableOperationRecordList(form));
    }


}
