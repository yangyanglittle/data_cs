package com.baidu.personalcode.crmdatads.service.datasynchronization.thread;

import com.baidu.personalcode.crmdatads.builder.BuilderPreparedStatementParam;
import com.baidu.personalcode.crmdatads.mapper.SynchronizationBaseMapper;
import com.baidu.personalcode.crmdatads.pojo.datasync.thread.JdbcThreadPo;
import com.baidu.personalcode.crmdatads.pojo.datasync.thread.MybatisThreadPo;
import com.baidu.personalcode.crmdatads.service.retry.ExceptionRetryService;
import com.baidu.personalcode.crmdatads.util.IdGenerator;
import com.baidu.personalcode.crmdatads.util.JsonUtil;
import com.baidu.personalcode.crmdatads.util.StringJoinerUtil;
import com.baidu.personalcode.crmdatads.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static com.baidu.personalcode.crmdatads.builder.DataBaseBuilder.getDataColumn;
import static com.baidu.personalcode.crmdatads.common.enums.ExceptionRetryStrategyEnum.INSERT_DATA_ERROR;

/**
 * @Author v_xueweidong
 * @Date 2022/9/16 18:03
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

    @Override
    public void asynchronousBatchExecution(JdbcThreadPo jdbcThreadPo) throws SQLException {

        String[] strings = jdbcThreadPo.getColumns1().toString().split(",");
        StringBuilder placeholderStr = new StringBuilder();
        for (int i = 0 ;i<strings.length;i++){
            placeholderStr.append("?").append(",");
        }
        placeholderStr.replace(placeholderStr.lastIndexOf(","), placeholderStr.length(), "");

        ExecutorService executorService = ThreadUtil.getExecutorService(10, 50,
                Integer.MAX_VALUE, "batch-insert-into-table");

        List<CompletableFuture<Void>> insertTableFutures = new ArrayList<>();

        for (int j = 0; j < jdbcThreadPo.getNum(); j++) {

            AtomicInteger atomicInteger = new AtomicInteger(
                    j * jdbcThreadPo.getJdbcDataSynchronizationOperation().getSize());

            insertTableFutures.add(CompletableFuture.runAsync(()->{
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
            }, executorService));
        }
        insertTableFutures.forEach(item -> CompletableFuture.allOf(item).join());
    }

    @Override
    public void asynchronousExecution(JdbcThreadPo jdbcThreadPo) throws SQLException {
        List<Future<String>> futures = new ArrayList<>();

        for (int j = 0; j < jdbcThreadPo.getNum(); j++) {
            // 这里线程参数测试随便写的
            ExecutorService executorService = ThreadUtil.getExecutorService(5, 100,
                    Integer.MAX_VALUE, "insert-into-table");

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
        AtomicInteger atomicInteger = mybatisThreadPo.getAtomicInteger();
        Map<String, Object> dataResultColumnValue = dataResultMaps.get(atomicInteger.get());
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

        StringJoiner columnSql = getDataColumn(dataResultMaps.get(atomicInteger.get()));

        String insertSql = "INSERT INTO "+ mybatisThreadPo.getTableName() + "("+columnSql+")" +
                "VALUES( " + columnValueSql + " )";

        mybatisThreadPo.setParam(insertSql);

        synchronizationBaseMapper.insert(insertSql);

        log.info("mybatisAsynchronousExecutionInvoke insert tableName:{},  count :{}",
                mybatisThreadPo.getTableName(),
                mybatisThreadPo.getAtomicInteger());
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
        mybatisThreadPo.setDataResultMaps(null);
        exceptionRetryService.insertExceptionRetry(INSERT_DATA_ERROR,
                IdGenerator.getUUID(),
                JsonUtil.serialize(mybatisThreadPo),
                errorJsonMsg.length() > 5000 ? errorJsonMsg.substring(0, 5000) : errorJsonMsg);
    }
}
