package org.kulorido.util;

import lombok.extern.slf4j.Slf4j;
import org.kulorido.common.constants.ResponseConstants;
import org.kulorido.exception.DataSynchronizationException;

import static org.kulorido.util.DataEmptyUtil.isNotEmpty;

/**
 * @Author kulorido
 * @Date 2099/12/31 14:18
 * @Version 1.0
 */
@Slf4j
public abstract class DataSynchronizationJudge {

    public static void isNull(Object object, String message){
        if (DataEmptyUtil.isEmpty(object)){
            throw new DataSynchronizationException(message);
        }
    }

    public static void notNull(Object object, String message){
        if (isNotEmpty(object)){
            throw new DataSynchronizationException(message);
        }
    }

    public static void isAnyParamEmpty(Object... objects){
        if (null == objects){
            throw new DataSynchronizationException(ResponseConstants.RES_MSG_NULL_BASIC);
        }
        for (Object o : objects){
            isNull(o, ResponseConstants.RES_MSG_NULL_BASIC);
        }
    }
}
