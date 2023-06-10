package org.kulorido.pojo.datasync;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


/**
 * @Author kulorido
 * @Version 1.0
 */
@Data
public class MybatisDataSynchronizationPo{

    private String tableName;

    private List<Map<String, Object>> dataResultMaps;

    private List<CompletableFuture<Void>> insertTableFutures;

    private String targetDataSourceName;
}
