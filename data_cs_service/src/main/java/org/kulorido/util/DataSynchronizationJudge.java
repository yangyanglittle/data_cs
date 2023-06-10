package org.kulorido.util;

import com.baidu.personalcode.crmdatads.common.constants.ResponseConstants;
import com.baidu.personalcode.crmdatads.exception.DataSynchronizationException;
import lombok.extern.slf4j.Slf4j;
import org.kulorido.common.constants.ResponseConstants;
import org.kulorido.exception.DataSynchronizationException;

import static com.baidu.personalcode.crmdatads.util.DataEmptyUtil.isAnyEmpty;
import static com.baidu.personalcode.crmdatads.util.DataEmptyUtil.isNotEmpty;
import static org.kulorido.util.DataEmptyUtil.isNotEmpty;

/**
 * @Author v_xueweidong
 * @Date 2022/9/16 14:18
 * @Version 1.0
 */
@Slf4j
public abstract class AbstractJudge {

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
}
