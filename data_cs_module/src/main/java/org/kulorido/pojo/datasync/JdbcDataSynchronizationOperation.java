package org.kulorido.pojo.datasync;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.kulorido.model.TableDbInfo;

import java.sql.Connection;
import java.sql.Statement;

/**
 * @Author kulorido
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
