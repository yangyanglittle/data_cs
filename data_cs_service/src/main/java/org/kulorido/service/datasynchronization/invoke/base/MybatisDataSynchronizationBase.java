package org.kulorido.service.datasynchronization.invoke.base;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.kulorido.mapper.SynchronizationBaseMapper;
import org.kulorido.model.SynchronizationBaseModel;
import org.kulorido.model.TableDbInfo;
import org.kulorido.pojo.datasync.DataSynchronizationPoBase;
import org.kulorido.pojo.datasync.MybatisDataSynchronizationPo;
import org.kulorido.service.datasynchronization.base.DBSynchronization;
import org.kulorido.service.datasynchronization.databaseoperations.MybatisDataSynchronization;
import org.kulorido.service.rejected.CustomRejectedExecutionHandler;
import org.kulorido.util.DataEmptyUtil;
import org.kulorido.util.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static org.kulorido.builder.DataBaseBuilder.getDataSourceName;
import static org.kulorido.common.constants.DataSourceConstants.MASTER_SOURCE;
import static org.kulorido.util.ThreadUtil.QUEUE_SIZE;

/**
 * @Author kulorido
 * @Date 2099/12/31 18:12
 * @Version 1.0
 */
@Slf4j
public abstract class MybatisDataSynchronizationBase extends DBSynchronization implements MybatisDataSynchronization {

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private SynchronizationBaseMapper synchronizationBaseMapper;

    @Autowired
    private CustomRejectedExecutionHandler rejectedExecutionHandler;

    @Override
    protected void doDataSynchronization(DataSynchronizationPoBase dataSynchronizationPoBase) throws SQLException {

        String targetDataSourceName = getDataSourceName(
                new TableDbInfo(dataSynchronizationPoBase.getConfigId()), false);

        // 切换目标表的数据源
        dynamicRoutingDataSource.setPrimary(targetDataSourceName);

        log.info("doDataSynchronization delete table dataSource :{}, key :{}",
                dynamicRoutingDataSource.determineDataSource(),
                DynamicDataSourceContextHolder.peek());

        // 清空目标表
        super.deleteTable(dataSynchronizationPoBase);

        //获取循环次数
        int num = (dataSynchronizationPoBase.getTotal() / dataSynchronizationPoBase.getSize()) + 1;

        String originDataSourceName = getDataSourceName(
                new TableDbInfo(dataSynchronizationPoBase.getConfigId()), true);

        ExecutorService executorService = ThreadUtil.getExecutorService(8, 16,
                QUEUE_SIZE, "mybatis-batch-insert-into-table", rejectedExecutionHandler);

        List<CompletableFuture<Void>> insertTableFutures = new ArrayList<>();
        MybatisDataSynchronizationPo mybatisDataSynchronizationPo =
                dataSynchronizationPoBase.getMybatisDataSynchronizationPo();
        mybatisDataSynchronizationPo.setInsertTableFutures(insertTableFutures);
        mybatisDataSynchronizationPo.setTargetDataSourceName(targetDataSourceName);
        for (int j = 0; j < num; j++) {

            SynchronizationBaseModel synchronizationBaseModel = new SynchronizationBaseModel(
                    dataSynchronizationPoBase.getMybatisDataSynchronizationPo().getTableName());
            synchronizationBaseModel.setPageParam(j + 1, dataSynchronizationPoBase.getSize());

            // 切换数据源头的数据源
            dynamicRoutingDataSource.setPrimary(originDataSourceName);

            List<Map<String, Object>> dataResultMaps = synchronizationBaseMapper.getOriginDataList(
                    synchronizationBaseModel);
            if (DataEmptyUtil.isEmpty(dataResultMaps)){
                log.info("dataResultMaps is empty, tableName:{}", dataSynchronizationPoBase.
                        getMybatisDataSynchronizationPo().getTableName());
                continue;
            }

            mybatisDataSynchronizationPo.setDataResultMaps(dataResultMaps);
            dataSynchronizationPoBase.setMybatisDataSynchronizationPo(mybatisDataSynchronizationPo);

            // 原本计划用mybatis SqlSessionFactory batch,不过有了jdbc了，就用jdbc了,
            // 这里用于5000以下的数据开并发慢慢跑吧,报错了直接丢异常重试表里面
            doMybatisDataSynchronization(dataSynchronizationPoBase, executorService);
        }

        insertTableFutures.forEach(item -> CompletableFuture.allOf(item).join());

        dynamicRoutingDataSource.setPrimary(MASTER_SOURCE);

    }
}
