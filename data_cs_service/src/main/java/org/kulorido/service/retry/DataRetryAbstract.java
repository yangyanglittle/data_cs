package org.kulorido.service.retry;

import org.kulorido.pojo.datasync.DataRetryPo;

public abstract class ExceptionRetryAbstract<T> implements ExceptionRetryInterface{

    @Override
    public String executeRetry(DataRetryPo dataRetryPo){
        return null;
    }
}
