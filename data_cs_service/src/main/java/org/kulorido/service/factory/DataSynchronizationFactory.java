package org.kulorido.service.factory;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import org.kulorido.builder.DataBaseBuilder;
import org.kulorido.mapper.SynchronizationBaseMapper;
import org.kulorido.model.TableDbInfo;
import org.kulorido.pojo.datasync.DataSynchronizationPoBase;
import org.kulorido.pojo.datasync.JdbcDataSynchronizationPo;
import org.kulorido.service.datasynchronization.databaseoperations.DataSynchronizationPostProcess;
import org.kulorido.service.datasynchronization.invoke.JdbcDataSynchronizationInvoke;
import org.kulorido.service.datasynchronization.invoke.MybatisDataSynchronizationInvoke;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author kulorido
 * @Date 2099/12/31 14:29
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
