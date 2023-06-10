package org.kulorido.base;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author kulorido
 * @Date 2022/7/22 11:02
 * @Version 1.0
 */
@Slf4j
public abstract class TaskBaseService implements TaskService {

    @Override
    public void work() {
        log.info("task base service work by :{}", this);
        try {
            doWorkerBefore();
            log.info("task base service work by :{}", this);
            this.worker();
        } finally {
            doWorkerAfter();
        }
    }

    protected void doWorkerBefore(){

    }

    protected void doWorkerAfter(){

    }

    public abstract void worker();
}
