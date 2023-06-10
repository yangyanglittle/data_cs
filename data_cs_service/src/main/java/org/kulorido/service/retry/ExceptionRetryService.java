package com.baidu.personalcode.crmdatads.service.retry;


import com.baidu.personalcode.crmdatads.common.enums.ExceptionRetryStrategyEnum;
import com.baidu.personalcode.crmdatads.common.enums.IsDealEnum;
import com.baidu.personalcode.crmdatads.mapper.retry.ExceptionRetryMapper;
import com.baidu.personalcode.crmdatads.retry.ExceptionRetryModel;
import com.baidu.personalcode.crmdatads.util.JsonUtil;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 异常重试service
 *
 * @author guanqi01@baidu.com
 * @date 2022/1/19 6:55 下午
 */
@Service
@Slf4j
public class ExceptionRetryService {

    @Autowired
    private ExceptionRetryMapper exceptionRetryMapper;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    /**
     * 插入异常重试表
     *
     * @param exceptionRetryStrategyEnum 异常重试策略枚举
     * @param objectId                   objectId 如伙伴Id，也可不填写
     * @param param                      重试参数
     * @author guanqi01@baidu.com
     * @date 2022/1/19 8:38 下午
     */
    public <T> void insertExceptionRetry(ExceptionRetryStrategyEnum exceptionRetryStrategyEnum
            , String objectId, T param, String exceptionReason) {
        try {
            Date date = new Date();
            ExceptionRetryModel exceptionRetry = new ExceptionRetryModel();
            exceptionRetry.setExceptionType(exceptionRetryStrategyEnum.getExceptionType());
            exceptionRetry.setExceptionMessage(exceptionRetryStrategyEnum.getExceptionMessage());
            exceptionRetry.setObjectId(objectId);
            exceptionRetry.setObjectType(exceptionRetryStrategyEnum.getObjectType());
            exceptionRetry.setDealNum(0);
            exceptionRetry.setMaxNum(exceptionRetryStrategyEnum.getMaxDealNum());
            exceptionRetry.setIsDeal(IsDealEnum.NO.getKey());
            exceptionRetry.setCreateTime(date);
            exceptionRetry.setUpdateTime(date);
            exceptionRetry.setParam(JsonUtil.serialize(param));
            exceptionRetry.setExceptionReason(exceptionReason);
            exceptionRetryMapper.insert(exceptionRetry);
        } catch (Exception e) {
            log.info("exception retry service use data source:{}, key:{}",
                    dynamicRoutingDataSource.determineDataSource(), DynamicDataSourceContextHolder.peek());
            log.error("insert exception retry fail;exceptionMessage={}, param = {}"
                    , exceptionRetryStrategyEnum.getExceptionMessage()
                    , param
                    , e);
        }
    }

}
