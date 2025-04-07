package com.wufeiqun.zeus.controller.cmdb;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wufeiqun.zeus.biz.cmdb.ServerFacade;
import com.wufeiqun.zeus.biz.cmdb.entity.ServerForm;
import com.wufeiqun.zeus.common.entity.CommonVo;
import com.wufeiqun.zeus.common.entity.SelectVO;
import com.wufeiqun.zeus.dao.Server;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wufeiqun
 * @date 2022-07-07
 */
@Slf4j
@RestController
@RequestMapping("/api/cmdb/server")
@RequiredArgsConstructor
public class ServerController {
    private final ServerFacade serverFacade;

    @PostMapping("/getServerList")
    public CommonVo<IPage<Server>> getPageableServerList(@RequestBody @Valid ServerForm.ServerQueryForm form){
        return CommonVo.success(serverFacade.getServerList(form));
    }

    @PostMapping("/update")
    public CommonVo<Object> updateServer(@RequestBody @Valid ServerForm.ServerUpdateForm form){
        serverFacade.updateServer(form);
        return CommonVo.success();
    }

    @GetMapping("/getSelectableServerList")
    public CommonVo<List<SelectVO>> getSelectableServerList() {
        return CommonVo.success(serverFacade.getSelectableServerList());
    }
}
