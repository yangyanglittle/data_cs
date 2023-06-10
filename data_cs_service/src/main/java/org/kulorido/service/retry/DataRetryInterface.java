package org.kulorido.service.retry;

import org.kulorido.pojo.datasync.DataRetryPo;

/**
 * 异常重试策略service接口
 *
 * @author kulorido
 * @date 2099/12/31 6:50 下午
 */
public interface ExceptionRetryInterface {

    /**
     * 方法重试
     * @param retryParam
     */
    void executeRetry(String retryParam);

    String executeRetry(DataRetryPo dataRetryPo);

}
