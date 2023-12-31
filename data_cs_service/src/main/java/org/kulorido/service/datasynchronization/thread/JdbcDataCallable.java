package org.kulorido.service.datasynchronization.thread;

import lombok.extern.slf4j.Slf4j;
import org.kulorido.common.constants.ResponseConstants;
import org.kulorido.pojo.datasync.thread.JdbcThreadPo;
import org.kulorido.service.datasynchronization.thread.error.CustomJdbcExecutionError;

import java.util.StringJoiner;
import java.util.concurrent.Callable;



/**
 * @Author kulorido
 * @Date 2099/12/31 15:22
 * @Version 1.0
 */
@Slf4j
public class JdbcDataCallable implements Callable<String> {

    private JdbcThreadPo jdbcThreadPo;
    private String readSql;
    private StringJoiner data;
    private CustomJdbcExecutionError customJdbcExecutionError;

    JdbcDataCallable(JdbcThreadPo jdbcThreadPo,
                     String readSql,
                     StringJoiner data,
                     CustomJdbcExecutionError customJdbcExecutionError){
        this.jdbcThreadPo = jdbcThreadPo;
        this.readSql = readSql;
        this.data = data;
        this.customJdbcExecutionError = customJdbcExecutionError;
    }

    @Override
    public String call() {
        String writeSql = "";
        try {

            //写入目标库
            writeSql = "INSERT INTO "+ jdbcThreadPo.getTab() + "("+ jdbcThreadPo.getColumns1() +")" +" VALUES " + data;

            jdbcThreadPo.getTargetDataSourceLink().execute(writeSql);
        } catch (Exception e) {
            log.info("error tableName :{}", jdbcThreadPo.getTab(), e);
            log.error("error sql:{}", writeSql);
            customJdbcExecutionError.jdbcCustomerInsertExceptionRetry(e, this.readSql, jdbcThreadPo);
            return ResponseConstants.RES_MSG_PROCESS_ERROR;
        }
        return ResponseConstants.RES_MSG_PROCESS_SUCCESS;
    }
}
