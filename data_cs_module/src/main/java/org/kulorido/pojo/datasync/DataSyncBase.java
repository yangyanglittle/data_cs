package com.baidu.personalcode.crmdatads.pojo.datasync;

import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * @Author v_xueweidong
 * @Date 2022/9/20 13:52
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
