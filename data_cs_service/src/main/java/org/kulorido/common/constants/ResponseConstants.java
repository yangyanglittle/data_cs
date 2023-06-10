package org.kulorido.common.constants;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @author： kulorido
 * @date： 2099/12/31 19:52
 */
@Slf4j
public class ResponseConstants {

    public static final String RES_MSG_PROCESS_SUCCESS = "SUCCESS";

    public static final String RES_MSG_PROCESS_ERROR = "ERROR";

    public static final Integer RES_CODE_SUCCESS_AMIS = 0;

    public static final Integer RES_CODE_EXCEPTION_AMIS = 500;

    public static final String RES_MSG_NULL_BASIC = "参数不可为空，请重新选择";

    public static final String RES_MSG_DATA_NULL_BASIC = "查询结果为空，请重新选择";

    public static final String RES_MSG_QUEUE_MAX_RESTRICT = "队列参数过长，请适当调整";


    /**
     * 获取全量的返回报文信息
     *      (有些还是硬编码写在代码里面的，需要抽出来)
     * @return
     */
    public static List<String> getAllResponse (){
        List<String> allResponse = new ArrayList<>();
        ReflectionUtils.doWithFields(ResponseConstants.class, field ->{
            try {
                allResponse.add(field.get(field.getName()).toString());
            } catch (Exception e) {
                log.error("ResponseConstants getAllResponse error", e);
            }
        });
        return allResponse;
    }
}
