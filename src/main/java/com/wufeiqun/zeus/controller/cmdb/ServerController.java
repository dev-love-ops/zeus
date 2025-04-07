package com.wufeiqun.zeus.controller.cmdb;

import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zeus.cmdb.Server;
import zeus.cmdb.pojo.ServerForms;
import zeus.pojo.CommonVo;
import zeus.pojo.SelectVO;

import javax.validation.Valid;
import java.util.List;

/**
 * @author wufeiqun
 * @date 2022-07-07
 */
@Slf4j
@RestController
@RequestMapping("/api/cmdb/server")
public class ServerController {
    private final ServerFacade serverFacade;

    @Autowired
    public ServerController(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    @PostMapping("/getServerList")
    public CommonVo<PageInfo<Server>> getServerList(@RequestBody @Valid ServerForms.ServerQueryForm form){
        return CommonVo.success(serverFacade.getServerList(form));
    }

    @PostMapping("/update")
    public CommonVo<Object> updateServer(@RequestBody @Valid ServerForms.ServerUpdateForm form){
        serverFacade.updateServer(form);
        return CommonVo.success();
    }

    @GetMapping("/getServerSelectList")
    public CommonVo<List<SelectVO>> getServerSelectList() {
        return CommonVo.success(serverFacade.getServerSelectList());
    }
}
