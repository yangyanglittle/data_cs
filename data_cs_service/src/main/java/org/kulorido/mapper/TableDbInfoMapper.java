package org.kulorido.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.kulorido.pojo.TableDbInfo;

import java.util.List;

public interface TableDbInfoDao {

    /** 检测同一配置下是否存在源数据库 */
    int checkMasterDb(@Param("configId") String configId, @Param("id") String id);

    /** [数据同步类型]检测同一配置下是否存在目标数据库 */
    int checkTargetDb(@Param("configId") String configId);

    /** 获取配置信息条数 */
    int getDbInfoTotal(@Param("configId") String configId);

    /** 根据ID删除 */
    int deleteById(@Param("id") String id);

    /** 根据配置ID批量删除 */
    int deleteByConfigIds(@Param("idList") List<String> idList);

    /** 插入一条数据 */
    int insertOne(TableDbInfo record);

    /** 根据ID更新所有数据 */
    int updateById(TableDbInfo record);

    /** 根据配置ID查询数据库列表 */
    List<TableDbInfo> queryByConfigId(@Param("configId") String configId);

    /** 根据配置ID查询源数据库表 */
    TableDbInfo queryMasterByConfigId(@Param("configId") String configId);

    @Select("select * from table_db_info;")
    List<TableDbInfo> queryAll();

    
}