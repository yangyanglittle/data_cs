package com.baidu.personalcode.crmdatads.pojo.datasync;

import com.baidu.personalcode.crmdatads.pojo.TableDbInfo;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.sql.Connection;
import java.sql.Statement;

/**
 * @Author v_xueweidong
 * @Date 2022/9/19 17:49
 * @Version 1.0
 */
@Data
@Builder
public class JdbcDataSynchronizationOperation {

    @Tolerate
    public JdbcDataSynchronizationOperation(){}

    private TableDbInfo md;

    private TableDbInfo td;

    private String tableName;

    private int tableDataTotal;

    private int size;

    private Statement originDataSourceLink;

    private Statement targetDataSourceLink;

    private Connection targetConn;

    private Connection masterConn;
}
