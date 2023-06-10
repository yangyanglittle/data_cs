package org.kulorido.model;

import lombok.Data;

/**
 * @Author kulorido
 * @Version 1.0
 */
@Data
public class SynchronizationBaseModel {

    public SynchronizationBaseModel(String tableName){
        this.tableName = tableName;
        if (null == pageNo || pageNo == 0){
            this.pageNo = 1;
            this.pageSize = 1000;
        }
        this.offset = (this.pageNo - 1) * this.pageSize;
    }

    private String tableName;

    private Integer pageNo;

    private Integer pageSize;

    private Integer offset;

    public void setPageParam(Integer pageNo, Integer pageSize){
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.offset = (pageNo - 1) * pageSize;
    }
}
