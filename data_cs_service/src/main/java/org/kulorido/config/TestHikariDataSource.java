package org.kulorido.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @package org.kulorido.config
 * @Author kulorido
 * @Data 2023/6/9 10:28
 */
public class TestHikariDataSource {
    public HikariDataSource initDataSource(){
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setConnectionTestQuery("SELECT 1");
        dataSource.setConnectionTimeout(60000);
        dataSource.setMinimumIdle(2);
        dataSource.setMaximumPoolSize(100);
        dataSource.setMaxLifetime(600000);
        dataSource.setIdleTimeout(300000);
        dataSource.setLeakDetectionThreshold(500000);
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/sync_data?useUnicode=true&characterEncoding=" +
                "utf-8&serverTimezone=Asia/Shanghai&useSSL=false&rewriteBatchedStatements=true");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        return dataSource;
    }

    public static void main(String[] args) {
        TestHikariDataSource testHikariDataSource = new TestHikariDataSource();
        HikariDataSource hikariDataSource = testHikariDataSource.initDataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(hikariDataSource);
        jdbcTemplate.execute("select * from table_config");
    }
}
