package org.kulorido.job;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.UUID;

@EnableScheduling
@Service
@Slf4j
public class TestWorker {

    @Scheduled(cron = "0/1 * * * * ? ")
    public void testTask() {
    }
}
