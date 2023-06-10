package org.kulorido.service;

import lombok.extern.slf4j.Slf4j;
import org.kulorido.pojo.datasync.DataSynchronizationPoBase;
import org.kulorido.pojo.datasync.MybatisDataSynchronizationPo;
import org.kulorido.service.factory.DataSynchronizationFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 表数据同步
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j
public class TableDataSyncService {

    @Autowired
    private DataSynchronizationFactory dataSynchronizationFactory;

    /**
     *
     * @param configId
     * @param tableName
     * @param size
     */
    public void syncTableData(String configId, String tableName, Integer size){

        Set<String> tableList = new HashSet<>(Arrays.asList(tableName.split(",")));
        DataSynchronizationPoBase dataSynchronizationPoBase = new DataSynchronizationPoBase();
        dataSynchronizationPoBase.setConfigId(configId);
        dataSynchronizationPoBase.setSize(size);
        dataSynchronizationPoBase.setTableList(tableList);
        // 默认使用队列
        dataSynchronizationPoBase.setQueue(true);
        // 默认用mybatis
        dataSynchronizationPoBase.setMybatisDataSynchronizationPo(new MybatisDataSynchronizationPo());

        log.info("syncTableData:{}", dataSynchronizationPoBase);

        dataSynchronizationFactory.getDataSynchronizationPostProcess(dataSynchronizationPoBase)
                .dataProcess(dataSynchronizationPoBase);
    }





}
