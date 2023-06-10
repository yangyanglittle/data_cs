package org.kulorido;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @package org.kulorido
 * @Author kulorido
 * @Data ${DATE} ${TIME}
 */
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"org.kulorido.*"})
@EnableTransactionManagement
@MapperScan(basePackages = {"org.kulorido.mapper"})
public class DataSyncApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext configurableApplicationContext =
                SpringApplication.run(DataSyncApplication.class, args);
        Object obj = configurableApplicationContext.getBean("testBean");
        System.out.println(obj);
    }
}