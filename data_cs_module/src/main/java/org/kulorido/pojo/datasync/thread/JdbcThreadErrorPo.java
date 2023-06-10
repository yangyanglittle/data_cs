package org.kulorido.pojo.datasync.thread;

import lombok.Data;

/**
 * @Author kulorido
 * @Version 1.0
 */
@Data
public class JdbcThreadErrorPo extends BaseErrorPo{

    public JdbcThreadErrorPo(){}

    public JdbcThreadErrorPo(String configId,
                             String readSql,
                             String tableName){
        this.configId = configId;
        this.readSql = readSql;
        this.tableName = tableName;
    }

    private String configId;

    private String readSql;

    private String tableName;
}
