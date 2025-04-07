package com.wufeiqun.zeus.biz.cmdb;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wufeiqun.zeus.biz.cmdb.entity.ServerForm;
import com.wufeiqun.zeus.common.entity.SelectVO;
import com.wufeiqun.zeus.common.enums.StatusEnum;
import com.wufeiqun.zeus.dao.Server;
import com.wufeiqun.zeus.service.IServerService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wufeiqun
 * @date 2022-07-07
 */
@Service
@RequiredArgsConstructor
public class ServerFacade {
    private final IServerService serverService;



    public IPage<Server> getServerList(ServerForm.ServerQueryForm form){
        Page<Server> serverPage = new Page<>(form.getPageNum(), form.getPageSize());

        QueryWrapper<Server> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.eq("status", StatusEnum.ENABLED.getCode());
        queryWrapper.like(StringUtils.isNotBlank(form.getInstanceName()), "instance_name", form.getInstanceName());
        queryWrapper.like(StringUtils.isNotBlank(form.getPrivateIp()), "private_ip", form.getPrivateIp());
        queryWrapper.like(StringUtils.isNotBlank(form.getPublicIp()), "public_ip", form.getPublicIp());

        return serverService.page(serverPage, queryWrapper);
    }

    public void updateServer(ServerForm.ServerUpdateForm form){

        Server server = new Server();
        server.setId(form.getId());
        server.setComment(form.getComment());

        serverService.updateById(server);
    }

    public List<SelectVO> getSelectableServerList(){
        return serverService.getServerMap().entrySet().stream().map(item -> {
            SelectVO vo = new SelectVO();

            if (Objects.nonNull(item)){
                vo.setValue(item.getKey());
                vo.setLabel(MessageFormat.format("{0}({1})", item.getValue().getInstanceName(), item.getValue().getPrivateIp()));
            }

            return vo;
        }).collect(Collectors.toList());
    }

}
