package com.baidu.personalcode.crmdatads.service.factory;

import com.baidu.personalcode.crmdatads.builder.DataBaseBuilder;
import com.baidu.personalcode.crmdatads.mapper.SynchronizationBaseMapper;
import com.baidu.personalcode.crmdatads.pojo.TableDbInfo;
import com.baidu.personalcode.crmdatads.pojo.datasync.DataSynchronizationPoBase;
import com.baidu.personalcode.crmdatads.pojo.datasync.JdbcDataSynchronizationPo;
import com.baidu.personalcode.crmdatads.service.datasynchronization.databaseoperations.DataSynchronizationPostProcess;
import com.baidu.personalcode.crmdatads.service.datasynchronization.invoke.JdbcDataSynchronizationInvoke;
import com.baidu.personalcode.crmdatads.service.datasynchronization.invoke.MybatisDataSynchronizationInvoke;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author v_xueweidong
 * @Date 2022/9/20 14:29
 * @Version 1.0
 */
@Service
public class DataSynchronizationFactory {

    @Autowired
    private JdbcDataSynchronizationInvoke jdbcDataSynchronizationInvoke;

    @Autowired
    private MybatisDataSynchronizationInvoke mybatisDataSynchronizationInvoke;

    @Autowired
    private SynchronizationBaseMapper synchronizationBaseMapper;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    public DataSynchronizationPostProcess getDataSynchronizationPostProcess(
            DataSynchronizationPoBase dataSynchronizationPoBase){

        Map<String, Integer> tableCountMaps = new HashMap<>(16);

        String datasourceName = DataBaseBuilder.getDataSourceName(
                new TableDbInfo(dataSynchronizationPoBase.getConfigId()), true);
        DataBaseBuilder.refreshThreadDataSource(datasourceName, dynamicRoutingDataSource);

        int tablesTotal = 0;
        for (String tableName : dataSynchronizationPoBase.getTableList()){
            int count = synchronizationBaseMapper.getTableCount(tableName);
            tableCountMaps.put(tableName, count);
            tablesTotal += count;
        }

        DataBaseBuilder.clearThreadDataSource();

        dataSynchronizationPoBase.setTableCountMaps(tableCountMaps);

        if (tablesTotal > 5000){
            dataSynchronizationPoBase.setJdbcDataSynchronizationPo(new JdbcDataSynchronizationPo());
            return jdbcDataSynchronizationInvoke;
        }
        return mybatisDataSynchronizationInvoke;
    }
}
