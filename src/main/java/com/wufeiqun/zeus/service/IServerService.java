package com.wufeiqun.zeus.service;

import com.wufeiqun.zeus.dao.Server;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 服务器 服务类
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
public interface IServerService extends IService<Server> {
    Map<String, Server> getServerMap();
}
