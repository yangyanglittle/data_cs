package org.kulorido.service.datasynchronization.invoke;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import lombok.extern.slf4j.Slf4j;
import org.kulorido.pojo.datasync.DataSynchronizationPoBase;
import org.kulorido.pojo.datasync.MybatisDataSynchronizationPo;
import org.kulorido.pojo.datasync.thread.DataThreadPo;
import org.kulorido.pojo.datasync.thread.MybatisThreadPo;
import org.kulorido.service.datasynchronization.invoke.base.MybatisDataSynchronizationBase;
import org.kulorido.service.datasynchronization.thread.DataThreadService;
import org.kulorido.service.thread.MybatisDataSynchronizationInvokeThread;
import org.kulorido.util.DataSynchronizationJudge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static org.kulorido.common.constants.DataSourceConstants.MASTER_SOURCE;
import static org.kulorido.common.constants.ResponseConstants.RES_MSG_NULL_BASIC;

/**
 * @Author kulorido
 * @Date 2099/12/31 18:12
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
        
        DataSynchronizationJudge.isNull(dataSynchronizationPoBase.getMybatisDataSynchronizationPo(), RES_MSG_NULL_BASIC);
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
            MybatisThreadPo mybatisThreadPo = new MybatisThreadPo(
                    mybatisDataSynchronizationPo.getTargetDataSourceName(),
                    mybatisDataSynchronizationPo.getDataResultMaps(),
                    mybatisDataSynchronizationPo.getTableName(),
                    dataSynchronizationPoBase.getConfigId(), i);

            mybatisDataSynchronizationPo.getInsertTableFutures().add(CompletableFuture.runAsync(
                    new MybatisDataSynchronizationInvokeThread(dataThreadService,
                            new DataThreadPo(mybatisThreadPo)), executorService)
            );
        }
        mybatisDataSynchronizationPo.getInsertTableFutures().forEach(item -> CompletableFuture.allOf(item).join());
    }
}
