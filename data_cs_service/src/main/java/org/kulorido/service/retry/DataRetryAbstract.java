package org.kulorido.service.retry;

import org.kulorido.pojo.datasync.DataRetryPo;

public abstract class DataRetryAbstract implements DataRetryInterface {

    @Override
    public String executeRetry(DataRetryPo dataRetryPo){
        return null;
    }
}
