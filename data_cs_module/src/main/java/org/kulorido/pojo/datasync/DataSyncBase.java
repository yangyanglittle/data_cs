package org.kulorido.pojo.datasync;

import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * @Author kulorido
 * @Version 1.0
 */
@Data
class DataSyncBase {

    private boolean queue;

    private String configId;

    private Set<String> tableList;

    private Integer size;

    private Integer total;

    private Map<String, Integer> tableCountMaps;
}
