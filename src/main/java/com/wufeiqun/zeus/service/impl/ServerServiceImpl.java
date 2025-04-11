package com.wufeiqun.zeus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wufeiqun.zeus.dao.Server;
import com.wufeiqun.zeus.dao.ServerMapper;
import com.wufeiqun.zeus.service.IServerService;
import org.springframework.stereotype.Service;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * <p>
 * 服务器 服务实现类
 * </p>
 *
 * @author wufeiqun
 * @since 2025-03-18
 */
@Service
public class ServerServiceImpl extends ServiceImpl<ServerMapper, Server> implements IServerService {

    @Override
    public Map<String, Server> getServerMap() {
        return this.list().stream().collect(toMap(Server::getInstanceId, item -> item));
    }
}
