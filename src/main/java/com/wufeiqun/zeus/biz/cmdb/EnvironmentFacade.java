package com.wufeiqun.zeus.biz.cmdb;

import com.wufeiqun.zeus.biz.cmdb.enums.EnvironmentEnum;
import com.wufeiqun.zeus.common.entity.SelectVO;
import com.wufeiqun.zeus.service.IEnvironmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wufeiqun
 * @date 2022-07-07
 */
@Service
@RequiredArgsConstructor
public class EnvironmentFacade {

    public List<EnvironmentEnum> getEnvironmentList(){
        return Arrays.stream(EnvironmentEnum.values()).toList();
    }

    public List<SelectVO> getEnvironmentSelectList(){
        return Arrays.stream(EnvironmentEnum.values()).map(item -> {
            SelectVO vo = new SelectVO();
            vo.setValue(item.getCode());
            vo.setLabel(item.getName());
            return vo;
        }).collect(Collectors.toList());
    }
}
