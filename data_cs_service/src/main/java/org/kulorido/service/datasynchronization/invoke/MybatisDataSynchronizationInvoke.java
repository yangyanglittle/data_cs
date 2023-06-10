package com.baidu.personalcode.crmdatads.service.datasynchronization.invoke;

import com.baidu.personalcode.crmdatads.pojo.datasync.DataSynchronizationPoBase;
import com.baidu.personalcode.crmdatads.pojo.datasync.MybatisDataSynchronizationPo;
import com.baidu.personalcode.crmdatads.pojo.datasync.thread.DataThreadPo;
import com.baidu.personalcode.crmdatads.pojo.datasync.thread.MybatisThreadPo;
import com.baidu.personalcode.crmdatads.service.datasynchronization.invoke.base.MybatisDataSynchronizationBase;
import com.baidu.personalcode.crmdatads.service.datasynchronization.thread.DataThreadService;
import com.baidu.personalcode.crmdatads.util.AbstractJudge;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static com.baidu.personalcode.crmdatads.common.constants.DataSourceConstants.MASTER_SOURCE;
import static com.baidu.personalcode.crmdatads.common.constants.ResponseConstants.RES_MSG_NULL_BASIC;

/**
 * @Author v_xueweidong
 * @Date 2022/9/16 18:12
 * @Version 1.0
 */
@Service
@Slf4j
public class MybatisDataSynchronizationInvoke extends MybatisDataSynchronizationBase {

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private DataThreadService dataThreadService;

    @Override
    protected void dataSynchronizationPreCheck(DataSynchronizationPoBase dataSynchronizationPoBase) {
        super.dataSynchronizationPreCheck(dataSynchronizationPoBase);
        
        AbstractJudge.isNull(dataSynchronizationPoBase.getMybatisDataSynchronizationPo(), RES_MSG_NULL_BASIC);
    }

    @Override
    protected void dataSynchronizationBeforePostProcess(DataSynchronizationPoBase dataSynchronizationPoBase) {

    }

    @Override
    protected void doDataSynchronizationPre(String tableName, DataSynchronizationPoBase dataSynchronizationPoBase) {

        dataSynchronizationPoBase.getMybatisDataSynchronizationPo().setTableName(tableName);

        dataSynchronizationPoBase.setTotal(dataSynchronizationPoBase.getTableCountMaps().get(tableName));

        log.info("doDataSynchronizationPre tableName:{}, count:{}",
                tableName,
                dataSynchronizationPoBase.getTableCountMaps().get(tableName));
    }

    @Override
    protected void dataSynchronizationAfterPostProcess(DataSynchronizationPoBase dataSynchronizationPoBase) {
        dynamicRoutingDataSource.setPrimary(MASTER_SOURCE);

        log.info("dataSynchronizationAfterPostProcess dynamicRoutingDataSource :{}",
                dynamicRoutingDataSource.determineDataSource());
    }

    @Override
    public void doMybatisDataSynchronization(DataSynchronizationPoBase dataSynchronizationPoBase,
                                             ExecutorService executorService) {

        MybatisDataSynchronizationPo mybatisDataSynchronizationPo =
                dataSynchronizationPoBase.getMybatisDataSynchronizationPo();

        for (int i = 0; i < mybatisDataSynchronizationPo.getDataResultMaps().size(); i++) {

            AtomicInteger atomicInteger = new AtomicInteger(i);

            mybatisDataSynchronizationPo.getInsertTableFutures().add(CompletableFuture.runAsync(()-> {
                try {
                    dataThreadService.dataThreadInvoke(new DataThreadPo(new MybatisThreadPo(
                            mybatisDataSynchronizationPo.getTargetDataSourceName(),
                            mybatisDataSynchronizationPo.getDataResultMaps(),
                            atomicInteger,
                            mybatisDataSynchronizationPo.getTableName(),
                            dataSynchronizationPoBase.getConfigId())));
                } catch (Exception e) {
                    log.error("dataThreadService.dataThreadInvoke error", e);
                }}, executorService));
        }
    }
}
