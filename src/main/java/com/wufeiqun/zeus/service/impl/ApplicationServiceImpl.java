package com.wufeiqun.zeus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wufeiqun.zeus.dao.Application;
import com.wufeiqun.zeus.dao.ApplicationMapper;
import com.wufeiqun.zeus.service.IApplicationService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 应用 服务实现类
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
@Service
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, Application> implements IApplicationService {

}
