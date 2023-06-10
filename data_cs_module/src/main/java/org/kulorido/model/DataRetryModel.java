package org.kulorido.model;

import lombok.Data;

import java.util.Date;

@Data
public class DataRetryModel {
    private Integer autoId;

    private String id;

    private String exceptionType;

    private String exceptionMessage;

    private String exceptionServiceId;

    private Integer retryNum = 0;

    private Integer maxRetryNum;

    private Boolean deal = false;

    private Date dealOkTime;

    private Date createTime;

    private Date updateTime;

    private String retryParam;

    private String exceptionReason;
}