package org.kulorido.service.datasynchronization.invoke.base;

import lombok.extern.slf4j.Slf4j;
import org.kulorido.pojo.datasync.DataSynchronizationPoBase;
import org.kulorido.service.datasynchronization.base.DBSynchronization;
import org.kulorido.service.datasynchronization.databaseoperations.JdbcDataSynchronization;
import org.springframework.stereotype.Service;

import static org.kulorido.common.constants.QuantitativeRestrictionsConstants.THREAD_NUM_SYNC_FLAG;

/**
 * @Author kulorido
 * @Date 2099/12/31 15:05
 * @Version 1.0
 */
@Slf4j
@Service
public abstract class JdbcDataSynchronizationBase extends DBSynchronization implements JdbcDataSynchronization {

    @Override
    protected void doDataSynchronization(DataSynchronizationPoBase jdbcDataSynchronizationPo) throws Exception{
        if (jdbcDataSynchronizationPo.getTotal() > THREAD_NUM_SYNC_FLAG &&
                jdbcDataSynchronizationPo.isQueue()) {

            jdbcDataSynchronizationPo.getJdbcDataSynchronizationPo()
                    .getJdbcDataSynchronizationOperation().setSize(5000);

            // 放队列里面跑去
            dataSynchronizationQueue(jdbcDataSynchronizationPo);
        } else {
            // 通过jdbc同步数据
            doJdbcDataSynchronization(jdbcDataSynchronizationPo);
        }
    }
}
