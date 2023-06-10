package org.kulorido.config;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author kulorido
 * @Data 2023/6/8 13:36
 */
@Configuration
@EnableCaching
@Slf4j
public class RedissonConfig {

    @Value("${redisson.host}")
    private String host;

    @Value("${redisson.port}")
    private String port;

//    @Value("${redisson.password}")
    private String password;

    @Value("${redisson.minimumIdleSize}")
    private int minimumIdleSize;

    @Value("${redisson.connectionPoolSize}")
    private int connectionPoolSize;

    @Bean(destroyMethod = "shutdown" ,name = "redissonClient")
    RedissonClient redisson() throws IOException {
        Config config = new Config();
        log.info(host);
        log.info(port);
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setKeepAlive(true)
                .setConnectionMinimumIdleSize(minimumIdleSize)
                .setDnsMonitoringInterval(30000L)
                .setConnectionPoolSize(connectionPoolSize);

        if (StringUtils.isNotBlank(password)) {
            serverConfig.setPassword(password);
        }

        return Redisson.create(config);
    }

    @Bean
    CacheManager cacheManager(@Qualifier("redissonClient") RedissonClient redissonClient) {
        Map config = new HashMap();
        return new RedissonSpringCacheManager(redissonClient, config);
    }
}
