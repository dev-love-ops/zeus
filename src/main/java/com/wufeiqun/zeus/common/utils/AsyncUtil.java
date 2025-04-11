package com.wufeiqun.zeus.common.utils;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * @author 吴飞群
 * @createTime 2022/05/31
 */
@Slf4j
@Component
public class AsyncUtil {

    private static final ThreadPoolExecutor SHARE_THREAD_POOL;

    static {
        long keepAliveTime = 1L;
        int maximumPoolSize = 100;
        int corePoolSize = 10;
        int workQueueSize = 1024;
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNamePrefix("zeus-async-pool").setDaemon(true).build();

        SHARE_THREAD_POOL = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, TimeUnit.HOURS,
                new LinkedBlockingQueue<>(workQueueSize), namedThreadFactory, handler);
    }


    public void doTask(Runnable task) {
        SHARE_THREAD_POOL.submit(task);
        log.info("AsyncUtil, 有新任务提交, 当前任务数量: {}", SHARE_THREAD_POOL.getActiveCount());
    }

    public long getActiveCount() {
        return SHARE_THREAD_POOL.getActiveCount();
    }

    public <V> Future<V> doTaskWithResult(Callable<V> task) {
        return SHARE_THREAD_POOL.submit(task);
    }
}
