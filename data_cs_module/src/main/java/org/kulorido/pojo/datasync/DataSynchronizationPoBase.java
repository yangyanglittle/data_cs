package com.baidu.personalcode.crmdatads.pojo.datasync;

import lombok.Data;

/**
 * @Author v_xueweidong
 * @Date 2022/9/16 14:30
 * @Version 1.0
 */
@Data
public class DataSynchronizationPoBase extends DataSyncBase{

    private JdbcDataSynchronizationPo jdbcDataSynchronizationPo;

    private MybatisDataSynchronizationPo mybatisDataSynchronizationPo;
}
