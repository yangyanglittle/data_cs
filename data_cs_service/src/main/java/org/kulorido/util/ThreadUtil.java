package org.kulorido.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
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

    public static ExecutorService getExecutorService(int corePoolSize,
                                                     int maxPoolSize,
                                                     int queueSize,
                                                     String threadNamePrefix,
                                                     RejectedExecutionHandler rejectedExecutionHandler){
        if (queueSize > CAPACITY){
            throw new DataSynchronizationThreadException(RES_MSG_QUEUE_MAX_RESTRICT);
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
