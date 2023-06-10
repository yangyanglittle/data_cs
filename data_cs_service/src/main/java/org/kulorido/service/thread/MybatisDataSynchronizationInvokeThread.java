package org.kulorido.service.thread;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.kulorido.pojo.datasync.thread.DataThreadPo;
import org.kulorido.service.datasynchronization.thread.DataThreadService;

/**
 * @package org.kulorido.service.thread
 * @Author kulorido
 * @Data 2023/6/8 16:41
 */
@Slf4j
@Data
public class MybatisDataSynchronizationInvokeThread implements Runnable{

    private DataThreadService dataThreadService;

    private volatile DataThreadPo dataThreadPo;

    public MybatisDataSynchronizationInvokeThread(){

    }

    public MybatisDataSynchronizationInvokeThread(DataThreadService dataThreadService, DataThreadPo dataThreadPo){
        this.dataThreadService = dataThreadService;
        this.dataThreadPo = dataThreadPo;
    }

    @Override
    public void run() {
        try {
            dataThreadService.dataThreadInvoke(dataThreadPo);
        } catch (Exception e) {
            log.error("dataThreadService.dataThreadInvoke error", e);
        }
    }
}
