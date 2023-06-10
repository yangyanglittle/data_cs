package com.baidu.personalcode.crmdatads.model;

import lombok.Data;

import java.util.Date;

@Data
public class DataSynchronizationQueueModel {

    private Long id;

    private String tableName;

    private Boolean isDeal;

    private Date createTime;

    private Date updateTime;

    private String param;

}