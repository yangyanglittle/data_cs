package com.baidu.personalcode.crmdatads.init;

import com.baidu.personalcode.crmdatads.common.cache.TableCacheData;
import com.baidu.personalcode.crmdatads.pojo.TableDbInfo;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.baidu.personalcode.crmdatads.common.constants.DataSourceConstants.ORIGIN_DATA_SOURCE;
import static com.baidu.personalcode.crmdatads.common.constants.DataSourceConstants.TARGET_DATA_SOURCE;

/**
 * 启动完成自动执行
 */

@Slf4j
@SuppressWarnings("rawtypes")
@Repository
public class InitDataSource implements ApplicationRunner {

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Override
    @Async
    public void run(ApplicationArguments args) {

        log.info("dynamicRoutingDataSource :{}", dynamicRoutingDataSource);
        log.info("dynamicRoutingDataSource run before data source:{}, size:{}",
                dynamicRoutingDataSource.getDataSources(),
                dynamicRoutingDataSource.getDataSources().size());

        // 设置默认的数据源
        Map<String, List<TableDbInfo>> dataSourceTableConfigs = TableCacheData.getTableDbConfigMap();

        // 根据不同的config，设置相对应的源头数据DB和目标数据DB
        dataSourceTableConfigs.forEach((k, v) -> v.forEach(dataSourceTableConfig -> {
            HikariConfig config = new HikariConfig();
            config.setUsername(dataSourceTableConfig.getDbUsername());
            config.setPassword(dataSourceTableConfig.getDbPassword());
            config.setJdbcUrl("jdbc:mysql://" + dataSourceTableConfig.getDbHost() + "/" +
                    dataSourceTableConfig.getDataBase() + "?characterEncoding=" + dataSourceTableConfig.getCharSet() +
                    "&serverTimezone=Asia/Shanghai&useSSL=false&zeroDateTimeBehavior=round" +
                    "&rewriteBatchedStatements=true");
            config.setDriverClassName("com.mysql.jdbc.Driver");
            // 1源数据库连接，2目标数据库连接
            String dataSourceName = "1".equalsIgnoreCase(dataSourceTableConfig.getDbType()) ?
                    k + "_" + ORIGIN_DATA_SOURCE : k + "_" + TARGET_DATA_SOURCE;
            config.setPoolName(dataSourceName);
            //默认配置
            config.validate();
            //添加数据源
            dynamicRoutingDataSource.addDataSource(dataSourceName, new HikariDataSource(config));
        }));

        log.info("dynamicRoutingDataSource run after data source:{}, size:{}",
                dynamicRoutingDataSource.getDataSources(),
                dynamicRoutingDataSource.getDataSources().size());
    }
}
