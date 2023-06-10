package org.kulorido.impl;


import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.kulorido.base.JobAbstractService;
import org.kulorido.mapper.DataSynchronizationQueueMapper;
import org.kulorido.model.DataSynchronizationQueueModel;
import org.kulorido.pojo.datasync.DataSynchronizationPoBase;
import org.kulorido.pojo.work.JobPo;
import org.kulorido.service.factory.DataSynchronizationFactory;
import org.kulorido.util.DataEmptyUtil;
import org.kulorido.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static org.kulorido.builder.DataBaseBuilder.clearThreadDataSource;
import static org.kulorido.builder.DataBaseBuilder.refreshThreadDataSource;
import static org.kulorido.common.constants.DataSourceConstants.MASTER_SOURCE;

/**
 * @Author kulorido
 * @Date 2099/12/31 13:21
 * @Version 1.0
 */
@Service(value = "dataSynchronizationJob")
@Slf4j
public class DataSynchronizationWorkerImpl extends JobAbstractService {

    @Autowired
    private DataSynchronizationQueueMapper dataSynchronizationQueueMapper;

    @Autowired
    private DataSynchronizationFactory dataSynchronizationFactory;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Override
    public void worker(JobPo jobPo) {

        try {
            log.info("dataSynchronizationTask dynamicRoutingDataSource :{}",
                    dynamicRoutingDataSource.determineDataSource());

            if (DataEmptyUtil.isEmpty(DynamicDataSourceContextHolder.peek())){
                refreshThreadDataSource(MASTER_SOURCE, dynamicRoutingDataSource);
            }

            DataSynchronizationQueueModel dataSynchronizationQueueModel = new DataSynchronizationQueueModel();
            dataSynchronizationQueueModel.setIsDeal(false);
            List<DataSynchronizationQueueModel> dataSynchronizationQueueModels =
                    dataSynchronizationQueueMapper.select(dataSynchronizationQueueModel);
            for (DataSynchronizationQueueModel item : dataSynchronizationQueueModels){
                DataSynchronizationPoBase dataSynchronizationPoBase =
                        JsonUtil.deserialize(item.getParam(), DataSynchronizationPoBase.class);
                dataSynchronizationFactory.getDataSynchronizationPostProcess(dataSynchronizationPoBase)
                        .dataProcess(dataSynchronizationPoBase);

                DataSynchronizationQueueModel updateModel = new DataSynchronizationQueueModel();
                updateModel.setIsDeal(true);
                updateModel.setId(item.getId());
                updateModel.setUpdateTime(new Date());
                dataSynchronizationQueueMapper.updateByPrimaryKey(updateModel);
            }
        } finally {
            clearThreadDataSource();
        }
    }
}
