package com.wufeiqun.zeus.controller.system;

import cn.hutool.core.lang.tree.Tree;
import com.wufeiqun.zeus.biz.system.DepartmentFacade;
import com.wufeiqun.zeus.common.entity.CommonVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/system/department/")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentFacade departmentFacade;

    @GetMapping("/getDepartmentTreeList")
    public CommonVo<List<Tree<String>>> getDepartmentTreeList() {
        return CommonVo.success(departmentFacade.getDepartmentTree());
    }

}
