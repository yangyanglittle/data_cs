package org.kulorido.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.kulorido.model.TableConfig;

import java.util.List;

public interface TableConfigMapper {

    /**
     * 查看数据库配置
     * @param configType
     * @return
     */
    @Select("select * from table_config where is_delete = 0 and config_type = #{configType} " +
            "order by create_time desc")
    List<TableConfig> queryAllConfig(@Param("configType") String configType);

    /**
     * 查看数据库配置
     * @param configId
     * @return
     */
    @Select("select * from table_config where is_delete = 0 and id = #{configId}")
    TableConfig queryConfigById(@Param("configId") String configId);

    @Insert({
            "insert into table_config (id, ",
            "name, remark, update_by, ",
            "update_time, create_by, ",
            "create_time, is_delete, ",
            "config_type)",
            "values (#{id,jdbcType=VARCHAR}, ",
            "#{name,jdbcType=VARCHAR}, #{remark,jdbcType=VARCHAR}, #{updateBy,jdbcType=VARCHAR}, ",
            "#{updateTime,jdbcType=TIMESTAMP}, #{createBy,jdbcType=VARCHAR}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{isDelete,jdbcType=VARCHAR}, ",
            "#{configType,jdbcType=VARCHAR})"
    })
    int insert(TableConfig record);
}