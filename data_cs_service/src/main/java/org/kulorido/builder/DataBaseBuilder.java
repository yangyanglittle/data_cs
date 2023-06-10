package org.kulorido.builder;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import org.kulorido.model.TableDbInfo;
import org.kulorido.util.DataEmptyUtil;

import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import static org.kulorido.common.constants.DataSourceConstants.ORIGIN_DATA_SOURCE;
import static org.kulorido.common.constants.DataSourceConstants.TARGET_DATA_SOURCE;

/**
 * @Author kulorido
 * @Date 2099/12/31 11:22
 * @Version 1.0
 */
public class DataBaseBuilder {

    public static String getDataSourceName(TableDbInfo tableDb, Boolean originSource){
        if (DataEmptyUtil.isNotEmpty(originSource)){
            if (originSource){
                return tableDb.getConfigId() + "_" + ORIGIN_DATA_SOURCE;
            }
            return tableDb.getConfigId() + "_" + TARGET_DATA_SOURCE;
        }
        return 1 == tableDb.getDbType() ?
                tableDb.getConfigId() + "_" + ORIGIN_DATA_SOURCE :
                tableDb.getConfigId() + "_" + TARGET_DATA_SOURCE;
    }

    public static StringJoiner getDataColumn(Map<String, Object> tableColumnNameMaps){
        Set<String> tableColumnSets = tableColumnNameMaps.keySet();
        StringJoiner columnSql = new StringJoiner(",");
        tableColumnSets.forEach(column ->{
            if ("delete".equals(column)){
                columnSql.add("`" + column + "`");
            } else {
                columnSql.add(column);
            }
        });
        return columnSql;
    }

    public static void refreshThreadDataSource(String dataSourceName,
                                               DynamicRoutingDataSource dynamicRoutingDataSource){
        DynamicDataSourceContextHolder.push(dataSourceName);
        dynamicRoutingDataSource.setPrimary(dataSourceName);
    }

    public static void clearThreadDataSource(){
        DynamicDataSourceContextHolder.clear();
    }
}
