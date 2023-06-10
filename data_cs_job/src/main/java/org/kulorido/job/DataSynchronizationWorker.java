package org.kulorido.job;

import lombok.extern.slf4j.Slf4j;
import org.kulorido.base.JobInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author kulorido
 * @Date 2099/12/31 14:08
 * @Version 1.0
 */
@EnableScheduling
@Service
@Slf4j
public class DataSynchronizationWorker {

    @Autowired
    @Qualifier(value = "dataSynchronizationJob")
    private JobInterface jobInterface;

    @Scheduled(cron = "0 0/30 * * * ?")
    public void dataSynchronizationTask() {
        try {
            log.info("dataSynchronizationTask locked success，dataSynchronizationTask start :" + new Date());
            new Thread(() -> jobInterface.work(null)).start();
            log.info("dataSynchronizationTask end " + new Date());
        } catch (Exception e) {
            log.error("dataSynchronizationTask error ", e);
        }
    }
}
