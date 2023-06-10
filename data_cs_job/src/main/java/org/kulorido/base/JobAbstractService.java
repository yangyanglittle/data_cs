package org.kulorido.base;

import lombok.extern.slf4j.Slf4j;
import org.kulorido.pojo.work.JobPo;

/**
 * @Author kulorido
 * @Date 2099/12/31 11:02
 * @Version 1.0
 */
@Slf4j
public abstract class JobAbstractService implements JobInterface {

    @Override
    public void work(JobPo jobPo) {
        log.info("job service work by :{}", this);
        try {
            // 多机部署的情况下这里要加分布式锁，避免多机跑重
            doWorkerBefore(jobPo);
            this.worker(jobPo);
        } finally {
            doWorkerAfter(jobPo);
        }
    }

    protected void doWorkerBefore(JobPo jobPo){

    }

    protected void doWorkerAfter(JobPo jobPo){

    }

    public abstract void worker(JobPo jobPo);
}
