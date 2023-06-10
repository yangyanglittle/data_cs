package org.kulorido.pojo.datasync.thread;

import lombok.Data;

/**
 * @Author kulorido
 * @Version 1.0
 */
@Data
public class DataThreadPo {

    public DataThreadPo(){}

    public DataThreadPo(MybatisThreadPo mybatisThreadPo){
        this.mybatisThreadPo = mybatisThreadPo;
    }

    public DataThreadPo(JdbcThreadPo jdbcThreadPo){
        this.jdbcThreadPo = jdbcThreadPo;
    }

    private MybatisThreadPo mybatisThreadPo;

    private JdbcThreadPo jdbcThreadPo;
}
