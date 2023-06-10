package com.baidu.personalcode.crmdatads.service.datasynchronization.databaseoperations;

import com.baidu.personalcode.crmdatads.pojo.datasync.DataSynchronizationPoBase;
import com.baidu.personalcode.crmdatads.service.datasynchronization.base.DataSynchronization;

/**
 * @Author v_xueweidong
 * @Date 2022/9/16 13:07
 * @Version 1.0
 */
public interface DataSynchronizationPostProcess extends DataSynchronization {

    /**
     * 数据同步
     */
    void dataProcess(DataSynchronizationPoBase dataSynchronizationPoBase);
}
