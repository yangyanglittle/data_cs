package org.kulorido.enums;

import lombok.Getter;
import lombok.Setter;


public enum DataRetryEnum {

    DATA_JDBC_RETRY ("DATA_JDBC_RETRY", "asyncDataRetryService", 5, "JDBC写入表失败重试"),

    DATA_MYBATIS_RETRY ("DATA_MYBATIS_RETRY", "asyncDataRetryService", 5, "MYBATIS写入表失败重试");

    DataRetryEnum(String exceptionType, String invokeBean, int retryCount, String errorMsg){
        this.exceptionType = exceptionType;
        this.invokeBean = invokeBean;
        this.retryCount = retryCount;
        this.errorMsg = errorMsg;
    }

    @Setter
    @Getter
    private String exceptionType;

    @Setter
    @Getter
    private String invokeBean;

    /**
     * 默认重试3次,调用方可以根据枚举自定义一个
     */
    @Setter
    @Getter
    private int retryCount = 3;

    /**
     * 异常参数，不可超过500
     */
    @Setter
    @Getter
    private String errorMsg;

    public static String getInvokeBean(String exceptionType){
        for (DataRetryEnum item : DataRetryEnum.values()) {
            if (item.getExceptionType().equals(exceptionType)){
                return item.getInvokeBean();
            }
        }
        return null;
    }
}
