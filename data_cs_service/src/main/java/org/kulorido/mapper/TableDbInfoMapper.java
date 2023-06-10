package org.kulorido.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.kulorido.model.TableDbInfo;

import java.util.List;

public interface TableDbInfoMapper {

    /** 根据配置ID查询数据库列表 */
    @Select("<script>" +
            "select * from table_db_info where config_id = #{configId} " +
            "<if test = \"dbType != dbType \">" +
            " and db_type = #{dbType} " +
            "</if>" +
            " ORDER BY db_type ASC " +
            "</script>")
    List<TableDbInfo> queryByConfigId(@Param("configId") String configId,
                                      @Param("dbType") Integer dbType);

    @Select("select * from table_db_info;")
    List<TableDbInfo> queryAll();

    @Insert({
            "insert into table_db_info (id, config_id,",
            "db_type, db_host, ",
            "db_username, db_password, ",
            "data_base, mysql_type, ",
            "char_set)",
            "values (#{id,jdbcType=VARCHAR}, #{configId,jdbcType=VARCHAR},",
            "#{dbType,jdbcType=TINYINT}, #{dbHost,jdbcType=VARCHAR}, ",
            "#{dbUsername,jdbcType=VARCHAR}, #{dbPassword,jdbcType=VARCHAR}, ",
            "#{dataBase,jdbcType=VARCHAR}, #{mysqlType,jdbcType=VARCHAR}, ",
            "#{charSet,jdbcType=VARCHAR})"
    })
    int insert(TableDbInfo record);
}