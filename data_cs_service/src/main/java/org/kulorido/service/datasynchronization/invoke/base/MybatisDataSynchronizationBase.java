package com.baidu.personalcode.crmdatads.service.datasynchronization.invoke.base;

import com.baidu.personalcode.crmdatads.mapper.SynchronizationBaseMapper;
import com.baidu.personalcode.crmdatads.model.SynchronizationBaseModel;
import com.baidu.personalcode.crmdatads.pojo.TableDbInfo;
import com.baidu.personalcode.crmdatads.pojo.datasync.DataSynchronizationPoBase;
import com.baidu.personalcode.crmdatads.pojo.datasync.MybatisDataSynchronizationPo;
import com.baidu.personalcode.crmdatads.service.datasynchronization.base.DBSynchronization;
import com.baidu.personalcode.crmdatads.service.datasynchronization.databaseoperations.MybatisDataSynchronization;
import com.baidu.personalcode.crmdatads.util.DataEmptyUtil;
import com.baidu.personalcode.crmdatads.util.ThreadUtil;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static com.baidu.personalcode.crmdatads.builder.DataBaseBuilder.getDataSourceName;
import static com.baidu.personalcode.crmdatads.common.constants.DataSourceConstants.MASTER_SOURCE;

/**
 * @Author v_xueweidong
 * @Date 2022/9/16 18:12
 * @Version 1.0
 */
@Slf4j
public abstract class MybatisDataSynchronizationBase extends DBSynchronization implements MybatisDataSynchronization {

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private SynchronizationBaseMapper synchronizationBaseMapper;

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


        ExecutorService executorService = ThreadUtil.getExecutorService(5, 20,
                Integer.MAX_VALUE, "mybatis-batch-insert-into-table");

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
