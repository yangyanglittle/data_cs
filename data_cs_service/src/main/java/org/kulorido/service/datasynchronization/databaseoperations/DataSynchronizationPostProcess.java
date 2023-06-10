package org.kulorido.service.datasynchronization.databaseoperations;

import org.kulorido.pojo.datasync.DataSynchronizationPoBase;
import org.kulorido.service.datasynchronization.base.DataSynchronization;

/**
 * @Author kulorido
 * @Date 2099/12/31 13:07
 * @Version 1.0
 */
public interface DataSynchronizationPostProcess extends DataSynchronization {

    /**
     * 数据同步
     */
    void dataProcess(DataSynchronizationPoBase dataSynchronizationPoBase);
}
