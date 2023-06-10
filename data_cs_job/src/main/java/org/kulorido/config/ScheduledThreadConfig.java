package org.kulorido.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.Task;
import org.springframework.scheduling.support.ScheduledMethodRunnable;

import java.util.Set;


@Configuration
@Slf4j
public class ScheduledThreadConfig implements SchedulingConfigurer, ApplicationRunner {

    @Autowired
    private ScheduledAnnotationBeanPostProcessor postProcessor;

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        try {
            // 拿到所有的task
            Set<ScheduledTask> scheduledTasks = postProcessor.getScheduledTasks();
            int pollSize = scheduledTasks.size();
            log.info("scheduled task size : {}", pollSize);

            for (ScheduledTask item : scheduledTasks) {
                Task task = item.getTask();
                ScheduledMethodRunnable runnable = (ScheduledMethodRunnable) task.getRunnable();
                Object taskObject = runnable.getTarget();
                log.info("scheduled task :{}. target:{}", taskObject, taskObject);
            }
            // 定时任务的核心线程数重新设置，为0的话默认为1
            pollSize = (pollSize == 0 ) ? 1 : scheduledTasks.size();
            taskScheduler.setPoolSize(pollSize);
            return taskScheduler;
        } catch (Exception e) {
            log.error("get scheduled error", e);
        }
        taskScheduler.setPoolSize(2);
        return taskScheduler;
    }


    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler());
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // do something
    }
}
