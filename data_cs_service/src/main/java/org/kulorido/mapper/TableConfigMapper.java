package org.kulorido.mapper;

import org.apache.ibatis.annotations.Param;
import org.kulorido.pojo.TableConfig;

import java.util.List;

public interface TableConfigDao {

    /** 根据ID删除 */
    int deleteById(@Param("id") String id);

    /** 根据ID批量删除 */
    int deleteByIds(@Param("idList") List<String> idList);

    /** 插入一条数据 */
    int insertOne(TableConfig record);

    /** 根据ID更新所有数据 */
    int updateById(TableConfig record);

    /** 根据主键查询一条数据 */
    TableConfig queryOneById(@Param("id") String id);

    /** 查询配置列表 */
    List<TableConfig> getList(TableConfig record);

    /** 查询自己权限下所有配置 */
    List<TableConfig> queryAllConfig(@Param("userId") String userId, @Param("conType") String conType);


    
}