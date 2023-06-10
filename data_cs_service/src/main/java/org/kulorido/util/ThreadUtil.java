package org.kulorido.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.kulorido.exception.DataSynchronizationBaseException;
import org.kulorido.exception.DataSynchronizationThreadException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.kulorido.common.constants.ResponseConstants.RES_MSG_QUEUE_MAX_RESTRICT;


/**
 * @Author kulorido
 * @Date 2099/12/31 15:23
 * @Version 1.0
 */
public class ThreadUtil {

    private static final int COUNT_BITS = Integer.SIZE - 18;

    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

    public static final int QUEUE_SIZE   = (1 << COUNT_BITS) / 2;

    /**
     * @param corePoolSize
     * @param maxPoolSize
     * @param queueSize
     * @param threadNamePrefix
     * @param rejectedExecutionHandler
     * @return
     */
    public static ExecutorService getExecutorService(int corePoolSize,
                                                     int maxPoolSize,
                                                     int queueSize,
                                                     String threadNamePrefix,
                                                     RejectedExecutionHandler rejectedExecutionHandler){
        if (queueSize > CAPACITY){
            throw new DataSynchronizationThreadException(RES_MSG_QUEUE_MAX_RESTRICT);
        }
        int core = Runtime.getRuntime().availableProcessors();
        if (core == 0){
            throw new DataSynchronizationBaseException.DataSynchronizationCoreException();
        }
        // 操作数据库，没必要线程放那么大，核心线程等于计算机的内核数就行
        if (corePoolSize > core){
            corePoolSize = core;
        }
        // 现在计算机有超线程技术，最大线程数设置为核心线程数的两倍即可，也许可以正负调整下，需要压测
        if (maxPoolSize > core << 1){
            maxPoolSize = core << 1;
        }
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
        if (null == rejectedExecutionHandler){
            return new ThreadPoolExecutor(corePoolSize, maxPoolSize,60, TimeUnit.SECONDS,
                    new LinkedBlockingDeque<>(queueSize), threadFactory, new ThreadPoolExecutor.AbortPolicy());
        }
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize,60, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(queueSize), threadFactory, rejectedExecutionHandler);
    }
}
