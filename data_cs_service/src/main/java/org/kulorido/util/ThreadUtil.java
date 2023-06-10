package org.kulorido.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.MDC;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.baidu.personalcode.crmdatads.common.constants.BceConstant.X_BCE_REQUEST_ID;

/**
 * @Author v_xueweidong
 * @Date 2022/9/16 15:23
 * @Version 1.0
 */
public class ThreadUtil {

    /**
     * 启动新线程，并带上老线程的request
     */
    public static void startNewThread(Runnable r) {
        new Thread(() -> {
            try {
                MDC.put(X_BCE_REQUEST_ID, UUID.randomUUID().toString());
                r.run();
            } finally {
                MDC.remove(X_BCE_REQUEST_ID);
            }
        }).start();
    }

    /**
     * 新建线程池
     * @param corePoolSize
     * @param maxPoolSize
     * @param queueSize
     * @param threadNamePrefix
     * maxPoolSize不要设置太大，有些服务设置了安全策略和QPS限制
     * @return
     */
    public static ExecutorService getExecutorService(int corePoolSize,
                                                     int maxPoolSize,
                                                     int queueSize,
                                                     String threadNamePrefix){
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();

        return new ThreadPoolExecutor(corePoolSize, maxPoolSize,60, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(queueSize),threadFactory,new ThreadPoolExecutor.AbortPolicy());
    }
}
