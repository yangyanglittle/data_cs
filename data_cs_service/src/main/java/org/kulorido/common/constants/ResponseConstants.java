package com.baidu.personalcode.crmdatads.common.constants;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @author： 薛卫东
 * @date： 2021-05-17 19:52
 */
@Slf4j
public class ResponseConstants {

    public static final String RES_MSG_PROCESS_SUCCESS = "SUCCESS";

    public static final String RES_MSG_PROCESS_ERROR = "ERROR";

    public static final Integer RES_CODE_SUCCESS_AMIS = 0;

    public static final Integer RES_CODE_EXCEPTION_AMIS = 500;

    public static final String RES_MSG_NULL_BASIC = "参数不可为空，请重新选择";

    public static final String RES_MSG_DATA_NULL_BASIC = "查询结果为空，请重新选择";


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
