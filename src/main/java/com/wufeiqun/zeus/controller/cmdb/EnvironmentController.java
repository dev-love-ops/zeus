package com.wufeiqun.zeus.controller.cmdb;

import com.wufeiqun.zeus.biz.cmdb.EnvironmentFacade;
import com.wufeiqun.zeus.biz.cmdb.enums.EnvironmentEnum;
import com.wufeiqun.zeus.common.entity.CommonVo;
import com.wufeiqun.zeus.common.entity.SelectVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wufeiqun
 * @date 2022-07-07
 */
@Slf4j
@RestController
@RequestMapping("/api/cmdb/environment")
public class EnvironmentController {
    private final EnvironmentFacade environmentFacade;

    @Autowired
    public EnvironmentController(EnvironmentFacade environmentFacade) {
        this.environmentFacade = environmentFacade;
    }


    @PostMapping("/getEnvironmentList")
    public CommonVo<List<EnvironmentEnum>> getEnvironmentList(){
        return CommonVo.success(environmentFacade.getEnvironmentList());
    }

    @GetMapping("/getSelectableEnvironmentList")
    public CommonVo<List<SelectVO>> getEnvironmentSelectList() {
        return CommonVo.success(environmentFacade.getEnvironmentSelectList());
    }


}
