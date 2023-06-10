package com.baidu.personalcode.crmdatads.service.datasynchronization.databaseoperations;

import com.baidu.personalcode.crmdatads.pojo.datasync.DataSynchronizationPoBase;

/**
 * @Author v_xueweidong
 * @Date 2022/9/16 14:05
 * @Version 1.0
 */
public interface JdbcDataSynchronization {

    /**
     * jdbc开始同步数据
     * @param jdbcDataSynchronizationPo
     * @throws Exception
     */
    void doJdbcDataSynchronization(DataSynchronizationPoBase jdbcDataSynchronizationPo) throws Exception;
}
