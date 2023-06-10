package org.kulorido.service.datasynchronization.databaseoperations;

import org.kulorido.pojo.datasync.DataSynchronizationPoBase;

import java.util.concurrent.ExecutorService;

/**
 * @Author kulorido
 * @Date 2099/12/31 14:05
 * @Version 1.0
 */
public interface MybatisDataSynchronization {

    void doMybatisDataSynchronization(DataSynchronizationPoBase dataSynchronizationPoBase,
                                      ExecutorService executorService);

}
