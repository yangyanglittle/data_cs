package org.kulorido.pojo.datasync;

import lombok.Data;

/**
 * @Author kulorido
 * @Version 1.0
 */
@Data
public class DataSynchronizationPoBase extends DataSyncBase{

    private JdbcDataSynchronizationPo jdbcDataSynchronizationPo;

    private MybatisDataSynchronizationPo mybatisDataSynchronizationPo;
}
