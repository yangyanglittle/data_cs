package org.kulorido.mapper;

import org.apache.ibatis.annotations.*;
import org.kulorido.model.DataRetryModel;

import java.util.List;

public interface DataRetryMapper {

    @Insert({
        "insert into data_retry (id, ",
        "exception_type, exception_message, ",
        "exception_service_id, retry_num, ",
        "max_retry_num, deal, ",
        "deal_ok_time, create_time, ",
        "update_time, retry_param, ",
        "exception_reason)",
        "values (#{id,jdbcType=VARCHAR}, ",
        "#{exceptionType,jdbcType=VARCHAR}, #{exceptionMessage,jdbcType=VARCHAR}, ",
        "#{exceptionServiceId,jdbcType=VARCHAR}, #{retryNum,jdbcType=TINYINT}, ",
        "#{maxRetryNum,jdbcType=TINYINT}, #{deal,jdbcType=BIT}, ",
        "#{dealOkTime,jdbcType=TIMESTAMP}, #{createTime,jdbcType=TIMESTAMP}, ",
        "#{updateTime,jdbcType=TIMESTAMP}, #{retryParam,jdbcType=LONGVARCHAR}, ",
        "#{exceptionReason,jdbcType=LONGVARCHAR})"
    })
    int insert(DataRetryModel record);

    @Select({
        "select * from data_retry " +
        "where auto_id > #{lastAutoId} " +
        "and deal = 0 and retry_num < max_retry_num " +
        "order by auto_id desc limit #{size}"
    })
    List<DataRetryModel> listExceptionRetry(@Param("lastAutoId") long lastAutoId, @Param("size") int size);

    @Update({"<script>",
        "update data_retry",
        "set retry_num = #{retryNum,jdbcType=TINYINT},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "<if test = \"deal != null \">" +
                    " ,deal = #{deal} " +
                    "</if>" +
            "<if test = \"dealOkTime != null \">" +
                    " ,dealOkTime = #{dealOkTime} " +
                    "</if>" +
        "where id = #{id}",
    "</script>"
    })
    int updateByPrimaryKey(DataRetryModel record);
}