package org.kulorido;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@EnableScheduling
@Service
@Slf4j
public class TestWorker {

    @Scheduled(cron = "0/1 * * * * ? ")
    public void exceptionRetryTask() {
        System.out.println(1);
    }
}
