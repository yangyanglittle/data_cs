package org.kulorido.service.datasynchronization.thread;

import lombok.extern.slf4j.Slf4j;
import org.kulorido.builder.BuilderPreparedStatementParam;
import org.kulorido.mapper.SynchronizationBaseMapper;
import org.kulorido.pojo.datasync.thread.JdbcThreadPo;
import org.kulorido.pojo.datasync.thread.MybatisThreadPo;
import org.kulorido.service.datasynchronization.thread.error.CustomJdbcExecutionError;
import org.kulorido.service.datasynchronization.thread.error.CustomMybatisExecutionError;
import org.kulorido.service.rejected.CustomRejectedExecutionHandler;
import org.kulorido.service.retry.ExceptionRetryService;
import org.kulorido.service.thread.AsynchronousBatchThread;
import org.kulorido.util.JsonUtil;
import org.kulorido.util.StringJoinerUtil;
import org.kulorido.util.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.kulorido.builder.DataBaseBuilder.getDataColumn;
import static org.kulorido.enums.DataRetryEnum.DATA_MYBATIS_RETRY;
import static org.kulorido.util.ThreadUtil.QUEUE_SIZE;

/**
 * @Author kulorido
 * @Date 2099/12/31 18:03
 * @Version 1.0
 */
@Service
@Slf4j
public class DataThreadServiceImpl extends DataAbstractThreadService{

    @Autowired
    private ExceptionRetryService exceptionRetryService;

    @Autowired
    private BuilderPreparedStatementParam builderPreparedStatementParam;

    @Autowired
    private SynchronizationBaseMapper synchronizationBaseMapper;

    @Autowired
    private CustomJdbcExecutionError customJdbcExecutionError;

    @Autowired
    private CustomMybatisExecutionError customMybatisExecutionError;

    @Autowired
    private CustomRejectedExecutionHandler rejectedExecutionHandler;

    @Override
    public void asynchronousBatchExecution(JdbcThreadPo jdbcThreadPo) throws SQLException {

        String[] strings = jdbcThreadPo.getColumns1().toString().split(",");
        StringBuilder placeholderStr = new StringBuilder();
        for (int i = 0 ;i<strings.length;i++){
            placeholderStr.append("?").append(",");
        }
        placeholderStr.replace(placeholderStr.lastIndexOf(","), placeholderStr.length(), "");

        ExecutorService executorService = ThreadUtil.getExecutorService(10, 50,
                QUEUE_SIZE, "batch-insert-into-table", rejectedExecutionHandler);

        List<CompletableFuture<Void>> insertTableFutures = new ArrayList<>();

        for (int j = 0; j < jdbcThreadPo.getNum(); j++) {

            AtomicInteger atomicInteger = new AtomicInteger(
                    j * jdbcThreadPo.getJdbcDataSynchronizationOperation().getSize());

            insertTableFutures.add(CompletableFuture.runAsync(
                    new AsynchronousBatchThread(jdbcThreadPo, builderPreparedStatementParam, customJdbcExecutionError,
                            placeholderStr, atomicInteger), executorService));
        }
        insertTableFutures.forEach(item -> CompletableFuture.allOf(item).join());
    }

    @Override
    public void asynchronousExecution(JdbcThreadPo jdbcThreadPo) throws SQLException {
        List<Future<String>> futures = new ArrayList<>();

        for (int j = 0; j < jdbcThreadPo.getNum(); j++) {
            // 这里线程参数测试随便写的
            ExecutorService executorService = ThreadUtil.getExecutorService(5, 100,
                    QUEUE_SIZE, "insert-into-table", rejectedExecutionHandler);

            //读取源库数据
            String readSql = "SELECT " + jdbcThreadPo.getColumns1() + " FROM " + jdbcThreadPo.getTab() + " LIMIT " +
                    j * jdbcThreadPo.getSize() + "," + jdbcThreadPo.getSize();

            StringJoiner data = StringJoinerUtil.getStringJoiner(readSql, jdbcThreadPo.getColList(),
                    jdbcThreadPo.getColTypeList(), jdbcThreadPo.getOriginDataSourceLink());

            futures.add(executorService.submit(new JdbcDataCallable(
                    jdbcThreadPo,
                    readSql,
                    data,
                    customJdbcExecutionError)));
        }

        futures.forEach(future->{
            try {
                log.info("do executor result :{}, tableName :{}", future.get(), jdbcThreadPo.getTab());
            } catch (InterruptedException | ExecutionException e) {
                log.error("futures get error", e);
            }
        });
    }

    @Override
    void mybatisAsynchronousExecutionInvoke(MybatisThreadPo mybatisThreadPo) {
        List<Map<String, Object>> dataResultMaps = mybatisThreadPo.getDataResultMaps();
        Map<String, Object> dataResultColumnValue = dataResultMaps.get(mybatisThreadPo.getI());
        StringJoiner columnValueSql = new StringJoiner(",");
        dataResultColumnValue.values().forEach((columnValue) -> {
            if (columnValue instanceof String || columnValue instanceof Date){
                columnValueSql.add("'" + columnValue.toString() + "'");
            } else if (columnValue instanceof Boolean){
                Boolean value = (Boolean) columnValue;
                if (value){
                    columnValueSql.add(String.valueOf(1));
                } else {
                    columnValueSql.add(String.valueOf(0));
                }
            }else {
                columnValueSql.add(columnValue.toString());
            }
        });

        StringJoiner columnSql = getDataColumn(dataResultMaps.get(mybatisThreadPo.getI()));

        String insertSql = "INSERT INTO "+ mybatisThreadPo.getTableName() + "("+columnSql+")" +
                "VALUES( " + columnValueSql + " )";

        mybatisThreadPo.setParam(insertSql);

        synchronizationBaseMapper.insert(insertSql);

        log.info("mybatisAsynchronousExecutionInvoke insert tableName:{},  count :{}",
                mybatisThreadPo.getTableName(),
                mybatisThreadPo.getI());
    }

    /**
     * 由于mybatis这里的处理是每次insert一条数据
     * 所以param需要记录单条失败的SQL记录以及数据源
     * @param e
     * @param mybatisThreadPo
     */
    @Override
    void mybatisAsynchronousExecutionError(Exception e, MybatisThreadPo mybatisThreadPo) {
        String errorJsonMsg = JsonUtil.serialize(e);
        errorJsonMsg = errorJsonMsg.length() > 1000 ? errorJsonMsg.substring(0, 1000) : errorJsonMsg;
        mybatisThreadPo.setDataResultMaps(null);
        exceptionRetryService.insertExceptionRetry(DATA_MYBATIS_RETRY, UUID.randomUUID().toString(),
                JsonUtil.serialize(mybatisThreadPo), errorJsonMsg);
    }
}
