package org.kulorido.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.kulorido.model.DataSynchronizationQueueModel;

import java.util.List;

public interface DataSynchronizationQueueMapper {


    @Delete({
        "delete from data_synchronization_queue",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int deleteByPrimaryKey(Long id);

    @Insert({
        "insert into data_synchronization_queue (table_name, ",
        "is_deal, create_time, ",
        "update_time, param)",
        "values ( #{tableName,jdbcType=VARCHAR}, ",
        "#{isDeal,jdbcType=BIT}, #{createTime,jdbcType=TIMESTAMP}, ",
        "#{updateTime,jdbcType=TIMESTAMP}, #{param,jdbcType=LONGVARCHAR})"
    })
    int insert(DataSynchronizationQueueModel record);

    @Select({
        "select",
        "id, table_name, is_deal, create_time, update_time, param",
        "from data_synchronization_queue",
        "where is_deal = #{isDeal}"
    })
    List<DataSynchronizationQueueModel> select(DataSynchronizationQueueModel record);

    @Update({
        "update data_synchronization_queue",
        "set is_deal = #{isDeal,jdbcType=BIT},",
          "update_time = #{updateTime,jdbcType=TIMESTAMP}",
        "where id = #{id,jdbcType=BIGINT}"
    })
    int updateByPrimaryKey(DataSynchronizationQueueModel record);
}