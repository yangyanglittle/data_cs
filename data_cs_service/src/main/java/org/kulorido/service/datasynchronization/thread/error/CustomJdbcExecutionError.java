package org.kulorido.service.datasynchronization.thread.error;

import lombok.extern.slf4j.Slf4j;
import org.kulorido.pojo.datasync.thread.JdbcThreadErrorPo;
import org.kulorido.pojo.datasync.thread.JdbcThreadPo;
import org.kulorido.service.retry.ExceptionRetryService;
import org.kulorido.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.kulorido.enums.DataRetryEnum.DATA_JDBC_RETRY;

/**
 * @Author kulorido
 * @Date 2099/12/31 19:15
 * @Version 1.0
 */
@Service
@Slf4j
public class CustomJdbcExecutionError extends CustomExecutionAbstractError{

    @Autowired
    private ExceptionRetryService exceptionRetryService;

    public void jdbcCustomerInsertExceptionRetry(Exception e, String readSql, JdbcThreadPo jdbcThreadPo){
        JdbcThreadErrorPo jdbcThreadErrorPo = new JdbcThreadErrorPo(jdbcThreadPo.getConfigId(), readSql,
                jdbcThreadPo.getTab());
        jdbcThreadErrorPo.setRetryFlag("jdbc");
        // 写入
        exceptionRetryService.insertExceptionRetry(DATA_JDBC_RETRY, UUID.randomUUID().toString(),
                JsonUtil.serialize(jdbcThreadErrorPo),
                JsonUtil.serialize(e.getMessage()));
    }
}
