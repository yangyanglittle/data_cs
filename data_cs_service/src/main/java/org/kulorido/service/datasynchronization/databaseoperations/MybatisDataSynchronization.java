package com.baidu.personalcode.crmdatads.service.datasynchronization.databaseoperations;

import com.baidu.personalcode.crmdatads.pojo.datasync.DataSynchronizationPoBase;

import java.util.concurrent.ExecutorService;

/**
 * @Author v_xueweidong
 * @Date 2022/9/16 14:05
 * @Version 1.0
 */
public interface MybatisDataSynchronization {

    void doMybatisDataSynchronization(DataSynchronizationPoBase dataSynchronizationPoBase,
                                      ExecutorService executorService);

}
