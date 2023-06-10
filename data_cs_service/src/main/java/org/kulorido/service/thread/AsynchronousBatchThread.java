package org.kulorido.service.thread;

import lombok.extern.slf4j.Slf4j;
import org.kulorido.builder.BuilderPreparedStatementParam;
import org.kulorido.pojo.datasync.thread.JdbcThreadPo;
import org.kulorido.service.datasynchronization.thread.error.CustomJdbcExecutionError;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @package org.kulorido.service.thread
 * @Author kulorido
 * @Data 2023/6/8 16:59
 */
@Slf4j
public class AsynchronousBatchThread implements Runnable{

    public AsynchronousBatchThread(){}

    public AsynchronousBatchThread(JdbcThreadPo jdbcThreadPo,
                                   BuilderPreparedStatementParam builderPreparedStatementParam,
                                   CustomJdbcExecutionError customJdbcExecutionError,
                                   StringBuilder placeholderStr,
                                   AtomicInteger atomicInteger){
        this.jdbcThreadPo = jdbcThreadPo;
        this.builderPreparedStatementParam = builderPreparedStatementParam;
        this.customJdbcExecutionError = customJdbcExecutionError;
        this.placeholderStr = placeholderStr;
        this.atomicInteger = atomicInteger;
    }

    private JdbcThreadPo jdbcThreadPo;

    private BuilderPreparedStatementParam builderPreparedStatementParam;

    private CustomJdbcExecutionError customJdbcExecutionError;

    private StringBuilder placeholderStr;

    private AtomicInteger atomicInteger;

    @Override
    public void run() {
        //读取源库数据
        String readSql = "SELECT " + jdbcThreadPo.getColumns1() + " FROM " +
                jdbcThreadPo.getJdbcDataSynchronizationOperation().getTableName() +
                " LIMIT " + atomicInteger.get() + "," +
                jdbcThreadPo.getJdbcDataSynchronizationOperation().getSize();
        try {
            builderPreparedStatementParam.setPreparedStatementParam(readSql,
                    jdbcThreadPo.getColList(),
                    jdbcThreadPo.getColTypeList(),
                    jdbcThreadPo.getJdbcDataSynchronizationOperation(),
                    placeholderStr,
                    jdbcThreadPo.getColumns1());
        } catch (SQLException e) {
            log.error("setPreparedStatementParam error", e);
            customJdbcExecutionError.jdbcCustomerInsertExceptionRetry(e, readSql, jdbcThreadPo);
        }
    }
}
