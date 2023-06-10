package com.example.demo.entity.model;

import java.util.Date;

public class DataRetryModel {
    private Integer autoId;

    private String id;

    private Byte exceptionType;

    private String exceptionMessage;

    private String exceptionServiceId;

    private Byte retryNum;

    private Byte maxRetryNum;

    private Boolean isDeal;

    private Date dealOkTime;

    private Date createTime;

    private Date updateTime;

    public Integer getAutoId() {
        return autoId;
    }

    public void setAutoId(Integer autoId) {
        this.autoId = autoId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public Byte getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(Byte exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage == null ? null : exceptionMessage.trim();
    }

    public String getExceptionServiceId() {
        return exceptionServiceId;
    }

    public void setExceptionServiceId(String exceptionServiceId) {
        this.exceptionServiceId = exceptionServiceId == null ? null : exceptionServiceId.trim();
    }

    public Byte getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(Byte retryNum) {
        this.retryNum = retryNum;
    }

    public Byte getMaxRetryNum() {
        return maxRetryNum;
    }

    public void setMaxRetryNum(Byte maxRetryNum) {
        this.maxRetryNum = maxRetryNum;
    }

    public Boolean getIsDeal() {
        return isDeal;
    }

    public void setIsDeal(Boolean isDeal) {
        this.isDeal = isDeal;
    }

    public Date getDealOkTime() {
        return dealOkTime;
    }

    public void setDealOkTime(Date dealOkTime) {
        this.dealOkTime = dealOkTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}