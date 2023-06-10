package org.kulorido.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.kulorido.request.BaseOperatorRequest;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @package org.kulorido.util
 * @Author kulorido
 * @Data 2023/6/9 13:56
 */
@Slf4j
public class DataSynchronizationReflectUtils {

    public static final List CREATE_INIT = Arrays.asList("createBy", "updateBy");

    public static final List DATE_INIT = Arrays.asList("createTime", "updateTime");

    public static final String SYSTEM_OPERATOR = "SYSTEM_OPERATOR";

    public static void initCreateAndUpdate(Object request, BaseOperatorRequest baseOperatorRequest, boolean createFlag){
        Method[] requestMethods = request.getClass().getMethods();
        for (Method method : requestMethods) {
            String methodName = method.getName().replace("set", "");
            String fieldName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
            // 为空，说明是新建，否则是修改
            if (createFlag){
                initCreate(method, fieldName, request, baseOperatorRequest);
            } else {
                initUpdate(method, fieldName, request, baseOperatorRequest);
            }
        }
    }

    @SneakyThrows
    private static void initCreate(Method method, String fieldName, Object request,
                                   BaseOperatorRequest baseOperatorRequest){
        if (CREATE_INIT.contains(fieldName)){
            if (DataEmptyUtil.isEmpty(baseOperatorRequest.getOperator())){
                method.invoke(request, SYSTEM_OPERATOR);
            } else {
                method.invoke(request, baseOperatorRequest.getOperator());
            }
        }
        if (DATE_INIT.contains(fieldName)){
            method.invoke(request, new Date());
        }
    }

    @SneakyThrows
    private static void initUpdate(Method method, String fieldName, Object request,
                                   BaseOperatorRequest baseOperatorRequest){
        if ("updateBy".equals(fieldName)){
            method.invoke(request, baseOperatorRequest.getOperator());
        }
        if ("updateTime".equals(fieldName)){
            method.invoke(request, new Date());
        }
    }
}
