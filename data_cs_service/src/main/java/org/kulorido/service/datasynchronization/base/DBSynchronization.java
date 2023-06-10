package com.baidu.personalcode.crmdatads.service.datasynchronization.base;

import com.baidu.personalcode.crmdatads.mapper.SynchronizationBaseMapper;
import com.baidu.personalcode.crmdatads.pojo.TableDbInfo;
import com.baidu.personalcode.crmdatads.pojo.datasync.DataSynchronizationPoBase;
import com.baidu.personalcode.crmdatads.pojo.datasync.JdbcDataSynchronizationPo;
import com.baidu.personalcode.crmdatads.util.DataEmptyUtil;
import com.baidu.personalcode.crmdatads.util.JdbcUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author v_xueweidong
 * @Date 2022/9/23 19:28
 * @Version 1.0
 */
@Slf4j
public abstract class DBSynchronization extends DataSynchronizationBase{

    @Autowired
    private SynchronizationBaseMapper synchronizationBaseMapper;

    protected Integer getTotalCount(String tableName, Statement originDataSourceLink) {
        int total = 0;
        if (originDataSourceLink != null){
            String totalSql = "SELECT count(*) total FROM " + tableName;
            ResultSet resultSet = null;
            try {
                resultSet = originDataSourceLink.executeQuery(totalSql);;
                if (resultSet.next()){
                    total = resultSet.getInt("total");
                }
            } catch (Exception e){
                log.error("getTotalCount executeQuery error", e);
            }finally {
                JdbcUtil.close(resultSet, null, null);
            }
        } else {
            return synchronizationBaseMapper.getTableCount(tableName);
        }
        return total;
    }

    protected List<String> getTypeList(TableDbInfo md, String tab, Statement originDataSourceLink) throws SQLException {
        List<String> colTypeList = new ArrayList<>();
        String typeSql = "SELECT DATA_TYPE dataType FROM information_schema.COLUMNS" +
                " WHERE TABLE_SCHEMA = '" + md.getDataBase() +
                "' AND TABLE_NAME = '"+ tab + "' ORDER BY ORDINAL_POSITION ASC";
        ResultSet resultSet = originDataSourceLink.executeQuery(typeSql);
        try {
            while (resultSet.next()){
                colTypeList.add(resultSet.getString("dataType").toUpperCase());
            }
        } finally {
            JdbcUtil.close(resultSet, null, null);
        }
        return colTypeList;
    }

    protected void deleteTable(DataSynchronizationPoBase dataSynchronizationPoBase) throws SQLException {
        JdbcDataSynchronizationPo jdbcDataSynchronizationPo =
                dataSynchronizationPoBase.getJdbcDataSynchronizationPo();
        if (DataEmptyUtil.isNotEmpty(jdbcDataSynchronizationPo)){
            // 清空目标表
            String delSql = "TRUNCATE TABLE " + jdbcDataSynchronizationPo.getJdbcDataSynchronizationOperation()
                    .getTableName();
            jdbcDataSynchronizationPo.getJdbcDataSynchronizationOperation().getTargetDataSourceLink()
                    .executeUpdate(delSql);
        } else {
            synchronizationBaseMapper.deleteTable(dataSynchronizationPoBase.getMybatisDataSynchronizationPo()
                    .getTableName());
        }
    }
}
