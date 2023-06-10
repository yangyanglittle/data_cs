package testjdbc;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.kulorido.config.TestHikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @package jdbcTest
 * @Author kulorido
 * @Data 2023/6/9 10:31
 */
public class TestJdbc {

    @Test
    public void test(){
        TestHikariDataSource testHikariDataSource = new TestHikariDataSource();
        HikariDataSource hikariDataSource = testHikariDataSource.initDataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(hikariDataSource);
        jdbcTemplate.execute("select * from table_config");
    }

    @Test
    public void a(){
        System.out.println(1);
    }
}
