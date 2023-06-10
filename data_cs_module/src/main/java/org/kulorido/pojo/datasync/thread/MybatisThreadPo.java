package org.kulorido.pojo.datasync.thread;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author kulorido
 * @Version 1.0
 */
@Data
public class MybatisThreadPo extends BaseErrorPo{

    public MybatisThreadPo(){}

    public MybatisThreadPo(String targetDataSourceName,
                           List<Map<String, Object>> dataResultMaps,
//                           AtomicInteger atomicInteger,
                           String tableName,
                           String configId){
        this.targetDataSourceName = targetDataSourceName;
        this.dataResultMaps = dataResultMaps;
//        this.atomicInteger = atomicInteger;
        this.tableName = tableName;
        this.configId = configId;
    }

    public MybatisThreadPo(String targetDataSourceName,
                           List<Map<String, Object>> dataResultMaps,
//                           AtomicInteger atomicInteger,
                           String tableName,
                           String configId, int i){
        this.targetDataSourceName = targetDataSourceName;
        this.dataResultMaps = dataResultMaps;
//        this.atomicInteger = atomicInteger;
        this.tableName = tableName;
        this.configId = configId;
        this.i = i;
    }

    private String targetDataSourceName;

    private List<Map<String, Object>> dataResultMaps;

    private AtomicInteger atomicInteger;

    private String tableName;

    /**
     * 异常重试的insert into SQL
     */
    private String param;

    private String configId;

    private int i;

}
