package org.kulorido;

import lombok.extern.slf4j.Slf4j;
import org.kulorido.enums.DataRetryEnum;
import org.kulorido.mapper.DataRetryMapper;
import org.kulorido.model.DataRetryModel;
import org.kulorido.service.retry.DataRetryInterface;
import org.kulorido.util.DataEmptyUtil;
import org.kulorido.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 异常重试定时任务
 *
 * @author kulorido
 * @date 2099/12/31 5:42 下午
 */
@EnableScheduling
@Service
@Slf4j
public class DataRetryWorker {

    @Autowired
    private DataRetryMapper dataRetryMapper;

    @Autowired
    private Map<String, DataRetryInterface> exceptionRetryStrategyServiceMap;

    /**
     * 异常重试定时任务 每5分钟执行一次
     *
     * @author kulorido
     * @date 2099/12/31 8:33 下午
     */
//    @Scheduled(cron = "0 0/5 * * * ?")
    public void exceptionRetryTask() {
        try {
            log.info("exceptionRetryTask start " + DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
            int querySize = 200;
            Integer autoId = 0;
            do {
                List<DataRetryModel> exceptionRetries = dataRetryMapper.listExceptionRetry(autoId, querySize);
                if (DataEmptyUtil.isEmpty(exceptionRetries)) {
                    break;
                }
                autoId = exceptionRetries.get(exceptionRetries.size() - 1).getAutoId();
                for (DataRetryModel item : exceptionRetries) {
                    retryException(item);
                }
            } while(true);

            log.info("exceptionRetryTask end " + DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            log.error("exceptionRetryTask error ", e);
        }
    }

    private void retryException(DataRetryModel dataRetryModel) {
        log.info("exceptionRetryTask 异常事件id:{},异常事件objectId:{},异常描述:{}", dataRetryModel.getId(),
                dataRetryModel.getExceptionServiceId(), dataRetryModel.getExceptionMessage());
        String invokeBean = DataRetryEnum.getInvokeBean(dataRetryModel.getExceptionType());
        if (null == invokeBean){
            log.error("retryException 异常任务重试失败 invokeBean is null exceptionRetry:{}",
                    dataRetryModel.getExceptionType());
            return;
        }
        DataRetryInterface retryStrategyService = exceptionRetryStrategyServiceMap.get(invokeBean);
        if (null == retryStrategyService){
            log.error("retryException 异常任务重试失败 invokeBean is null exceptionRetry:{}",
                    dataRetryModel.getExceptionType());
            return;
        }
        boolean retryResult = false;
        try {
            retryStrategyService.executeRetry(dataRetryModel.getRetryParam());
            retryResult = true;
        } catch (Exception e) {
            log.error("exceptionRetryTask 异常任务重试异常:{}", dataRetryModel.getExceptionMessage(), e);
        }
        updateDealNum(dataRetryModel, retryResult);
        if (dataRetryModel.getRetryNum() + 1 >= dataRetryModel.getMaxRetryNum()) {
            if (!retryResult) {
                log.error("exceptionRetryTask 重试超过最大重试次数【{}】次仍失败的异常数据：{}"
                        , dataRetryModel.getMaxRetryNum(), dataRetryModel);
            }
        }
    }

    private void updateDealNum(DataRetryModel dataRetryModel, boolean retryResult) {
        DataRetryModel upt = new DataRetryModel();
        upt.setId(dataRetryModel.getId());
        if (retryResult) {
            upt.setDeal(true);
            upt.setDealOkTime(new Date());
        }
        upt.setRetryNum(dataRetryModel.getRetryNum() + 1);
        upt.setUpdateTime(new Date());
        dataRetryMapper.updateByPrimaryKey(upt);
    }

}
