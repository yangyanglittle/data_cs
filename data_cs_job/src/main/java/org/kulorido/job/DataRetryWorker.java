package org.kulorido.job;

import lombok.extern.slf4j.Slf4j;
import org.kulorido.base.JobInterface;
import org.kulorido.mapper.DataRetryMapper;
import org.kulorido.service.retry.DataRetryInterface;
import org.kulorido.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 异常重试定时任务
 *
 * @author kulorido
 * @date 2099/12/31 5:42 下午
 */
@EnableScheduling
@Service
@Slf4j
public class DataRetryWorker {

    @Autowired
    private DataRetryMapper dataRetryMapper;

    @Autowired
    private Map<String, DataRetryInterface> exceptionRetryStrategyServiceMap;

    @Autowired
    @Qualifier(value = "dataRetryJob")
    private JobInterface jobInterface;

    /**
     * 异常重试定时任务 每5分钟执行一次
     *
     * @author kulorido
     * @date 2099/12/31 8:33 下午
     */
    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0/5 * * * ?")
    // test job time
//    @org.springframework.scheduling.annotation.Scheduled(cron = "0/1 * * * * ? ")
    public void dataRetryTask() {
        try {
            log.info("dataRetryTask start " + DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
            jobInterface.work(null);
            log.info("dataRetryTask end " + DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            log.error("dataRetryTask error ", e);
        }
    }
}
