package com.baidu.personalcode.crmdatads.service.datasynchronization.base;

import com.baidu.bce.crm.bizcom.lock.db.TaskLocker;
import com.baidu.bce.crm.bizcom.lock.db.service.TaskLockService;
import com.baidu.personalcode.crmdatads.common.constants.DataSourceConstants;
import com.baidu.personalcode.crmdatads.mapper.DataSynchronizationQueueMapper;
import com.baidu.personalcode.crmdatads.model.DataSynchronizationQueueModel;
import com.baidu.personalcode.crmdatads.pojo.datasync.DataSynchronizationPoBase;
import com.baidu.personalcode.crmdatads.pojo.datasync.JdbcDataSynchronizationOperation;
import com.baidu.personalcode.crmdatads.pojo.datasync.JdbcDataSynchronizationPo;
import com.baidu.personalcode.crmdatads.service.datasynchronization.databaseoperations.DataSynchronizationPostProcess;
import com.baidu.personalcode.crmdatads.util.AbstractJudge;
import com.baidu.personalcode.crmdatads.util.DataEmptyUtil;
import com.baidu.personalcode.crmdatads.util.DateCstUtils;
import com.baidu.personalcode.crmdatads.util.JsonUtil;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import static com.baidu.personalcode.crmdatads.common.constants.ResponseConstants.RES_MSG_NULL_BASIC;


/**
 * @Author v_xueweidong
 * @Date 2022/9/16 13:06
 * @Version 1.0
 */
@Slf4j
@Service
public abstract class DataSynchronizationBase implements DataSynchronizationPostProcess {

    @Autowired
    private TaskLockService taskLockService;

    @Autowired
    private DataSynchronizationQueueMapper dataSynchronizationQueueMapper;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Override
    public void dataProcess(DataSynchronizationPoBase dataSynchronizationPoBase) {

        dataSynchronizationPreCheck(dataSynchronizationPoBase);

        try {

            dynamicRoutingDataSource.setPrimary(DataSourceConstants.MASTER_SOURCE);

            dataSynchronizationBeforePostProcess(dataSynchronizationPoBase);

            log.info("doDataSynchronization begin time is {}",
                    DateCstUtils.utcToCSTStr(new Date(), "yyyy-MM-dd HH:mm:ss"));

            for (String tableName : dataSynchronizationPoBase.getTableList()){
                try (TaskLocker taskLocker = new TaskLocker(taskLockService, tableName,
                        30 * 1000 * 6, 1)) {
                    if (taskLocker.isLocked()){

                        doDataSynchronizationPre(tableName, dataSynchronizationPoBase);

                        if (DataEmptyUtil.isEmpty(dataSynchronizationPoBase.getTotal())){
                            continue;
                        }

                        doDataSynchronization(dataSynchronizationPoBase);
                    } else {
                        log.error("tableName :{}, is locked", tableName);
                    }
                }
            }

        } catch (Exception e){
            log.error("dataProcess doDataSynchronization error", e);
        }finally {
            log.info("doDataSynchronization end time is {}",
                    DateCstUtils.utcToCSTStr(new Date(), "yyyy-MM-dd HH:mm:ss"));

            dataSynchronizationAfterPostProcess(dataSynchronizationPoBase);

        }
    }

    /**
     * 前置校验
     */
    protected void dataSynchronizationPreCheck(DataSynchronizationPoBase dataSynchronizationPoBase){
        log.info("syncTableData dataSynchronizationPo :{}", dataSynchronizationPoBase);
        AbstractJudge.isNull(dataSynchronizationPoBase, RES_MSG_NULL_BASIC);
        AbstractJudge.isAnyParamEmpty(dataSynchronizationPoBase.getConfigId(), dataSynchronizationPoBase.getTableList());
    }

    /**
     * 前面干点啥
     * @param dataSynchronizationPoBase
     */
    protected abstract void dataSynchronizationBeforePostProcess(DataSynchronizationPoBase dataSynchronizationPoBase) throws Exception;

    /**
     * 可以开始干活了
     * @param dataSynchronizationPoBase
     */
    protected abstract void doDataSynchronization(DataSynchronizationPoBase dataSynchronizationPoBase) throws Exception;

    /**
     * 干活前在做点事情
     * @param tableName
     * @param dataSynchronizationPoBase
     * @throws Exception
     */
    protected abstract void doDataSynchronizationPre(String tableName, DataSynchronizationPoBase dataSynchronizationPoBase) throws Exception;

    /**
     * 使用队列进行数据的写入，由定时任务进行调度
     * @param dataSynchronizationPoBase
     */
    protected void dataSynchronizationQueue(DataSynchronizationPoBase dataSynchronizationPoBase) {

        JdbcDataSynchronizationOperation jdbcDataSynchronizationOperation = dataSynchronizationPoBase
                .getJdbcDataSynchronizationPo().getJdbcDataSynchronizationOperation();
        DataSynchronizationPoBase insertDataSynchronizationPoBase = new DataSynchronizationPoBase();
        JdbcDataSynchronizationOperation insertJdbcDataSynchronizationOperation =
                new JdbcDataSynchronizationOperation();
        JdbcDataSynchronizationOperation jdbcData = dataSynchronizationPoBase
                .getJdbcDataSynchronizationPo().getJdbcDataSynchronizationOperation();
        insertJdbcDataSynchronizationOperation.setMd(jdbcData.getMd());
        insertJdbcDataSynchronizationOperation.setTableName(jdbcData.getTableName());
        insertJdbcDataSynchronizationOperation.setTd(jdbcData.getTd());
        insertJdbcDataSynchronizationOperation.setSize(jdbcData.getSize());
        JdbcDataSynchronizationPo jdbcDataSynchronizationPo = new JdbcDataSynchronizationPo();
        insertDataSynchronizationPoBase.setJdbcDataSynchronizationPo(jdbcDataSynchronizationPo);
        jdbcDataSynchronizationPo.setJdbcDataSynchronizationOperation(insertJdbcDataSynchronizationOperation);
        insertDataSynchronizationPoBase.setQueue(false);
        insertDataSynchronizationPoBase.setConfigId(dataSynchronizationPoBase.getConfigId());
        insertDataSynchronizationPoBase.setTableList(new HashSet<>(Collections.singletonList(jdbcData.getTableName())));
        insertDataSynchronizationPoBase.setSize(dataSynchronizationPoBase.getSize());
        insertDataSynchronizationPoBase.setTotal(dataSynchronizationPoBase.getTotal());
        insertDataSynchronizationPoBase.setTableCountMaps(dataSynchronizationPoBase.getTableCountMaps());
        DataSynchronizationQueueModel dataSynchronizationQueueModel = new DataSynchronizationQueueModel();
        dataSynchronizationQueueModel.setTableName(jdbcDataSynchronizationOperation.getTableName());
        dataSynchronizationQueueModel.setParam(JsonUtil.serialize(insertDataSynchronizationPoBase));
        dataSynchronizationQueueModel.setIsDeal(false);
        dataSynchronizationQueueModel.setCreateTime(new Date());
        dataSynchronizationQueueModel.setUpdateTime(new Date());
        dataSynchronizationQueueMapper.insert(dataSynchronizationQueueModel);

        log.info("dataSynchronizationQueue table name :{}", dataSynchronizationQueueModel.getTableName());
    }

    /**
     * 后面干点啥
     * @param dataSynchronizationPoBase
     */
    protected abstract void dataSynchronizationAfterPostProcess(DataSynchronizationPoBase dataSynchronizationPoBase);
}
