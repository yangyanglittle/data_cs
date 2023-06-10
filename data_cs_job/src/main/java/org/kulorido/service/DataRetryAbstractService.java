package org.kulorido.service;

import lombok.extern.slf4j.Slf4j;
import org.kulorido.base.JobAbstractService;
import org.kulorido.enums.DataRetryEnum;
import org.kulorido.exception.DataSynchronizationRetryException;
import org.kulorido.mapper.DataRetryMapper;
import org.kulorido.model.DataRetryModel;
import org.kulorido.service.retry.DataRetryInterface;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class DataRetryAbstractService extends JobAbstractService {


    @Autowired
    private DataRetryMapper dataRetryMapper;

    @Autowired
    private Map<String, DataRetryInterface> exceptionRetryStrategyServiceMap;

    public List<DataRetryModel> getDataRetryModel(int querySize, Integer autoId){
        if (querySize == 0){
            querySize = 200;
        }
        if (null == autoId){
            autoId = 0;
        }
        return dataRetryMapper.listExceptionRetry(autoId, querySize);
    }

    public DataRetryInterface getDataRetryInterface(DataRetryModel dataRetryModel){
        String invokeBean = DataRetryEnum.getInvokeBean(dataRetryModel.getExceptionType());
        if (null == invokeBean){
            log.error("retryException 异常任务重试失败 invokeBean is null exceptionRetry:{}",
                    dataRetryModel.getExceptionType());
            throw new DataSynchronizationRetryException("获取beanName失败，请联系管理员");
        }
        DataRetryInterface retryStrategyService = exceptionRetryStrategyServiceMap.get(invokeBean);
        if (null == retryStrategyService){
            log.error("retryException 异常任务重试失败 invokeBean is null exceptionRetry:{}",
                    dataRetryModel.getExceptionType());
            throw new DataSynchronizationRetryException("获取bean失败，请联系管理员");
        }
        return retryStrategyService;
    }

    public void retryException(DataRetryModel dataRetryModel) {
        log.info("dataRetryTask 异常事件id:{},异常事件exceptionServiceId:{},异常描述:{}", dataRetryModel.getId(),
                dataRetryModel.getExceptionServiceId(), dataRetryModel.getExceptionMessage());
        DataRetryInterface retryStrategyService = this.getDataRetryInterface(dataRetryModel);
        boolean retryResult = false;
        try {
            retryStrategyService.executeRetry(dataRetryModel.getRetryParam());
            retryResult = true;
        } catch (Exception e) {
            log.error("dataRetryTask 异常任务重试异常:{}", dataRetryModel.getExceptionMessage(), e);
        }
        this.afterRetryException(dataRetryModel, retryResult);
    }

    public void afterRetryException(DataRetryModel dataRetryModel, boolean retryResult) {
        DataRetryModel upt = new DataRetryModel();
        upt.setId(dataRetryModel.getId());
        if (retryResult) {
            upt.setDeal(true);
            upt.setDealOkTime(new Date());
        }
        upt.setRetryNum(dataRetryModel.getRetryNum() + 1);
        upt.setUpdateTime(new Date());
        dataRetryMapper.updateByPrimaryKey(upt);

        if (dataRetryModel.getRetryNum() + 1 >= dataRetryModel.getMaxRetryNum()) {
            if (!retryResult) {
                log.error("dataRetryTask 重试超过最大重试次数【{}】次仍失败的异常数据：{}", dataRetryModel.getMaxRetryNum(),
                        dataRetryModel);
            }
        }
    }
}
