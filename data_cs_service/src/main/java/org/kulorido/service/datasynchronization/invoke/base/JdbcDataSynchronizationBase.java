package com.baidu.personalcode.crmdatads.service.datasynchronization.invoke.base;

import com.baidu.personalcode.crmdatads.pojo.datasync.DataSynchronizationPoBase;
import com.baidu.personalcode.crmdatads.service.datasynchronization.base.DBSynchronization;
import com.baidu.personalcode.crmdatads.service.datasynchronization.databaseoperations.JdbcDataSynchronization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import static com.baidu.personalcode.crmdatads.common.constants.QuantitativeRestrictionsConstants.THREAD_NUM_SYNC_FLAG;

/**
 * @Author v_xueweidong
 * @Date 2022/9/16 15:05
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
