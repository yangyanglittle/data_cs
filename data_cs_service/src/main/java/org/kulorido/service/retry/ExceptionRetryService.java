package org.kulorido.service.retry;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.kulorido.enums.DataRetryEnum;
import org.kulorido.mapper.DataRetryMapper;
import org.kulorido.model.DataRetryModel;
import org.kulorido.util.DataEmptyUtil;
import org.kulorido.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * 异常重试service
 *
 * @author kulorido
 * @date 2099/12/31 6:55 下午
 */
@Service
@Slf4j
public class ExceptionRetryService {

    @Autowired
    private DataRetryMapper dataRetryMapper;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    /**
     * @param dataRetryEnum
     * @param exceptionServiceId
     * @param retryParam
     * @param exceptionReason
     * @param <T>
     */
    public <T> void insertExceptionRetry(DataRetryEnum dataRetryEnum, String exceptionServiceId, T retryParam,
                                         String exceptionReason) {
        try {
            if (DataEmptyUtil.isEmpty(exceptionServiceId)){
                log.error("exceptionServiceId is null");
                return;
            }
            if (DataEmptyUtil.isEmpty(retryParam)){
                log.error("retryParam is null");
                return;
            }
            Date date = new Date();
            DataRetryModel exceptionRetry = new DataRetryModel();
            exceptionRetry.setId(UUID.randomUUID().toString());
            exceptionRetry.setExceptionType(dataRetryEnum.getExceptionType());
            exceptionRetry.setExceptionMessage(dataRetryEnum.getErrorMsg());
            exceptionRetry.setExceptionServiceId(exceptionServiceId);
            exceptionRetry.setMaxRetryNum(dataRetryEnum.getRetryCount());
            exceptionRetry.setCreateTime(date);
            exceptionRetry.setUpdateTime(date);
            exceptionRetry.setRetryParam(JsonUtil.serialize(retryParam));
            exceptionRetry.setExceptionReason(exceptionReason);
            dataRetryMapper.insert(exceptionRetry);
        } catch (Exception e) {
            log.info("exception retry service use data source:{}, key:{}",
                    dynamicRoutingDataSource.determineDataSource(), DynamicDataSourceContextHolder.peek());
            log.error("insert data retry fail;exceptionMessage={}", e.getMessage(), e);
        }
    }
}
