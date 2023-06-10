package org.kulorido.service.datasynchronization.databaseoperations;

import org.kulorido.pojo.datasync.DataSynchronizationPoBase;

/**
 * @Author kulorido
 * @Date 2099/12/31 14:05
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
