package org.kulorido;

import com.baidu.personalcode.crmdatads.mapper.DataSynchronizationQueueMapper;
import com.baidu.personalcode.crmdatads.model.DataSynchronizationQueueModel;
import com.baidu.personalcode.crmdatads.pojo.datasync.DataSynchronizationPoBase;
import com.baidu.personalcode.crmdatads.service.factory.DataSynchronizationFactory;
import com.baidu.personalcode.crmdatads.task.base.TaskBaseService;
import com.baidu.personalcode.crmdatads.util.DataEmptyUtil;
import com.baidu.personalcode.crmdatads.util.JsonUtil;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.baidu.personalcode.crmdatads.builder.DataBaseBuilder.clearThreadDataSource;
import static com.baidu.personalcode.crmdatads.builder.DataBaseBuilder.refreshThreadDataSource;
import static com.baidu.personalcode.crmdatads.common.constants.DataSourceConstants.MASTER_SOURCE;

/**
 * @Author v_xueweidong
 * @Date 2022/9/27 13:21
 * @Version 1.0
 */
@Service(value = "dataSynchronizationTask")
@Slf4j
public class DataSynchronizationWorkerImpl extends TaskBaseService {

    @Autowired
    private DataSynchronizationQueueMapper dataSynchronizationQueueMapper;

    @Autowired
    private DataSynchronizationFactory dataSynchronizationFactory;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Override
    public void worker() {

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
