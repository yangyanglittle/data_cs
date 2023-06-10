package org.kulorido.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.kulorido.model.SynchronizationBaseModel;

import java.util.List;
import java.util.Map;

/**
 * @Author kulorido
 * @Date 2099/12/31 19:24
 * @Version 1.0
 */
public interface SynchronizationBaseMapper {

    @Select("<script> " +
            "select count(1) from ${tableName}; " +
            "</script>")
    int getTableCount(@Param("tableName") String tableName);

    @Insert(" ${insertSql} ")
    int insert(@Param("insertSql") String insertSql);

    @Select(" select * from ${model.tableName} limit #{model.offset}, #{model.pageSize}")
    List<Map<String, Object>> getOriginDataList(@Param("model") SynchronizationBaseModel synchronizationBaseModel);

    @Select(" ${readSql} ")
    List<Map<String, Object>> getOriginDataBySql(@Param("readSql") String readSql);

    @Delete(" TRUNCATE TABLE ${tableName};")
    int deleteTable(@Param("tableName") String tableName);
}
