package org.kulorido.service.datasynchronization.thread.error;

import lombok.extern.slf4j.Slf4j;
import org.kulorido.pojo.datasync.thread.MybatisThreadPo;
import org.kulorido.service.retry.ExceptionRetryService;
import org.kulorido.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.kulorido.enums.DataRetryEnum.DATA_MYBATIS_RETRY;

@Service
@Slf4j
public class CustomMybatisExecutionError extends CustomExecutionAbstractError{

    @Autowired
    private ExceptionRetryService exceptionRetryService;

    public void mybatisCustomerInsertExceptionRetry(Exception e, MybatisThreadPo mybatisThreadPo){
//        String errorJsonMsg = JsonUtil.serialize(e);
//        mybatisThreadPo.setDataResultMaps(null);
//        exceptionRetryService.insertExceptionRetry(DATA_MYBATIS_RETRY, UUID.randomUUID().toString(),
//                JsonUtil.serialize(mybatisThreadPo), errorJsonMsg.length() > 5000 ?
//                        errorJsonMsg.substring(0, 5000) : errorJsonMsg);
    }
}
