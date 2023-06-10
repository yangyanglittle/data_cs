package org.kulorido.service.rejected;

import lombok.extern.slf4j.Slf4j;
import org.kulorido.pojo.datasync.thread.DataThreadPo;
import org.kulorido.service.datasynchronization.thread.JdbcDataCallable;
import org.kulorido.service.thread.AsynchronousBatchThread;
import org.kulorido.service.thread.MybatisDataSynchronizationInvokeThread;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @package org.kulorido.service.rejected
     * @Author kulorido
 * @Data 2023/6/8 15:55
 * 这个handler是spring管理的
 * 本来想分开写三个handler，但是我懒，所以放一起了
 * todo 这个没测，不知道 instanceof 有用没
 */
@Service
@Slf4j
public class CustomRejectedExecutionHandler implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        log.info("begin CustomRejectedExecutionHandler rejectedExecution");
        BlockingQueue<Runnable> blockingQueue = executor.getQueue();
        if (null != blockingQueue){
            while (!blockingQueue.isEmpty()){
                try {
                    Runnable runnable = blockingQueue.take();
                    if (r instanceof MybatisDataSynchronizationInvokeThread){
                        MybatisDataSynchronizationInvokeThread mybatisDataSynchronizationInvokeThread =
                                (MybatisDataSynchronizationInvokeThread) runnable;
                        DataThreadPo dataThreadPo = mybatisDataSynchronizationInvokeThread.getDataThreadPo();
                        // do something
                    }
                    if (r instanceof AsynchronousBatchThread){
                        AsynchronousBatchThread asynchronousBatchThread = (AsynchronousBatchThread) runnable;
                        // do something
                    }
                    if (r instanceof JdbcDataCallable){
                        JdbcDataCallable jdbcDataCallable = (JdbcDataCallable) runnable;
                        // do something
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
