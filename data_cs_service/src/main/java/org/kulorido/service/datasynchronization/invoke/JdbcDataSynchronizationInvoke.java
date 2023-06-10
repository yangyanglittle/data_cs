package com.baidu.personalcode.crmdatads.service.datasynchronization.invoke;

import com.baidu.personalcode.crmdatads.pojo.TableDbInfo;
import com.baidu.personalcode.crmdatads.pojo.datasync.DataSynchronizationPoBase;
import com.baidu.personalcode.crmdatads.pojo.datasync.JdbcDataSynchronizationOperation;
import com.baidu.personalcode.crmdatads.pojo.datasync.JdbcDataSynchronizationPo;
import com.baidu.personalcode.crmdatads.pojo.datasync.thread.DataThreadPo;
import com.baidu.personalcode.crmdatads.pojo.datasync.thread.JdbcThreadPo;
import com.baidu.personalcode.crmdatads.service.TableDbInfoService;
import com.baidu.personalcode.crmdatads.service.datasynchronization.databaseoperations.JdbcDataSynchronization;
import com.baidu.personalcode.crmdatads.service.datasynchronization.invoke.base.JdbcDataSynchronizationBase;
import com.baidu.personalcode.crmdatads.service.datasynchronization.thread.DataThreadService;
import com.baidu.personalcode.crmdatads.service.datasynchronization.thread.local.JdbcDataSyncThreadLocal;
import com.baidu.personalcode.crmdatads.util.AbstractJudge;
import com.baidu.personalcode.crmdatads.util.DataEmptyUtil;
import com.baidu.personalcode.crmdatads.util.JdbcUtil;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static com.baidu.personalcode.crmdatads.common.constants.ResponseConstants.RES_MSG_DATA_NULL_BASIC;
import static com.baidu.personalcode.crmdatads.common.constants.ResponseConstants.RES_MSG_NULL_BASIC;


/**
 * @Author v_xueweidong
 * @Date 2022/9/16 14:13
 * @Version 1.0
 */
@Service
@Slf4j
public class JdbcDataSynchronizationInvoke extends JdbcDataSynchronizationBase {

    @Autowired
    private TableDbInfoService tableDbInfoService;

    @Autowired
    private DataThreadService dataThreadService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Override
    protected void dataSynchronizationPreCheck(DataSynchronizationPoBase dataSynchronizationPoBase) {
        super.dataSynchronizationPreCheck(dataSynchronizationPoBase);

        JdbcDataSynchronizationPo jdbcDataSynchronizationPo = dataSynchronizationPoBase.getJdbcDataSynchronizationPo();
        AbstractJudge.isNull(jdbcDataSynchronizationPo, RES_MSG_NULL_BASIC);
    }

    @Override
    protected void dataSynchronizationBeforePostProcess(DataSynchronizationPoBase dataSynchronizationPoBase) throws SQLException {

        log.info("dynamicRoutingDataSource :{}", dynamicRoutingDataSource.determineDataSource());

        List<TableDbInfo> tableDbInfos = tableDbInfoService.queryByConfigId(dataSynchronizationPoBase.getConfigId());

        AbstractJudge.isNull(tableDbInfos, RES_MSG_DATA_NULL_BASIC);

        TableDbInfo md = tableDbInfos.get(0);
        TableDbInfo td = tableDbInfos.get(1);
        Connection masterConn = JdbcUtil.getConn2(md);
        Statement originDataSourceLink =  masterConn.createStatement();
        Connection targetConn = JdbcUtil.getConn2(td);
        Statement targetDataSourceLink = targetConn.createStatement();

        int size = dataSynchronizationPoBase.getSize();

        //获取每次读取数据条数
        if (DataEmptyUtil.isEmpty(size)){
            size = 1000;
        }

        JdbcDataSynchronizationOperation jdbcDataSynchronizationOperation = JdbcDataSynchronizationOperation
                .builder()
                .md(md)
                .td(td)
                .originDataSourceLink(originDataSourceLink)
                .targetDataSourceLink(targetDataSourceLink)
                .targetConn(targetConn)
                .masterConn(masterConn)
                .size(size)
                .build();

        dataSynchronizationPoBase.getJdbcDataSynchronizationPo().
                setJdbcDataSynchronizationOperation(jdbcDataSynchronizationOperation);

    }

    @Override
    protected void doDataSynchronizationPre(String tableName, DataSynchronizationPoBase dataSynchronizationPoBase) {
        dataSynchronizationPoBase.setTotal(dataSynchronizationPoBase.getTableCountMaps().get(tableName));
        dataSynchronizationPoBase.getJdbcDataSynchronizationPo().getJdbcDataSynchronizationOperation()
                .setTableName(tableName);
    }

    @Override
    public void doJdbcDataSynchronization(DataSynchronizationPoBase jdbcDataSynchronizationPo) throws Exception {

        JdbcDataSynchronizationOperation jdbcDataSynchronizationOperation =
                jdbcDataSynchronizationPo.getJdbcDataSynchronizationPo().getJdbcDataSynchronizationOperation();

        String tableName = jdbcDataSynchronizationOperation.getTableName();

        //获取表字段
        List<String> colList = new ArrayList<>();
        StringJoiner columns1 = new StringJoiner(",");
        String colSql = "SELECT COLUMN_NAME colName FROM information_schema.COLUMNS" +
                " WHERE TABLE_SCHEMA = '" + jdbcDataSynchronizationOperation.getMd().getDataBase() +
                "' AND TABLE_NAME = '"+ tableName + "' ORDER BY ORDINAL_POSITION ASC";
        ResultSet mrs = null;

        Statement originDataSourceLink = jdbcDataSynchronizationOperation.getOriginDataSourceLink();
        Statement targetDataSourceLink = jdbcDataSynchronizationOperation.getTargetDataSourceLink();

        try {
            mrs = originDataSourceLink.executeQuery(colSql);
            while (mrs.next()){
                String colName = mrs.getString("colName");
                colList.add(colName);
                columns1.add(colName);
            }
        } finally {
            JdbcUtil.close(mrs, null ,null);
        }

        //获取循环次数
        int num = (jdbcDataSynchronizationPo.getTotal() / jdbcDataSynchronizationOperation.getSize()) + 1;

        // 清空目标表
        super.deleteTable(jdbcDataSynchronizationPo);

        //获取表字段类型
        List<String> colTypeList = super.getTypeList(jdbcDataSynchronizationOperation.getMd(), tableName,
                originDataSourceLink);

        dataThreadService.dataThreadInvoke(new DataThreadPo(
                JdbcThreadPo.builder()
                        .colList(colList)
                        .colTypeList(colTypeList)
                        .columns1(columns1)
                        .jdbcDataSynchronizationOperation(jdbcDataSynchronizationOperation)
                        .num(num)
                        .originDataSourceLink(originDataSourceLink)
                        .tab(tableName)
                        .targetDataSourceLink(targetDataSourceLink)
                        .size(jdbcDataSynchronizationOperation.getSize())
                        .queue(jdbcDataSynchronizationPo.isQueue())
                        .configId(jdbcDataSynchronizationPo.getConfigId())
                        .totalCount(jdbcDataSynchronizationPo.getTotal())
                        .build())
        );
    }

    @Override
    protected void dataSynchronizationAfterPostProcess(DataSynchronizationPoBase dataSynchronizationPoBase) {
        JdbcDataSynchronizationOperation jdbcDataSynchronizationOperation = dataSynchronizationPoBase
                .getJdbcDataSynchronizationPo().getJdbcDataSynchronizationOperation();
        if (DataEmptyUtil.isNotEmpty(jdbcDataSynchronizationOperation)){
            JdbcUtil.close(jdbcDataSynchronizationOperation.getOriginDataSourceLink(),
                    jdbcDataSynchronizationOperation.getMasterConn());
            JdbcUtil.close(jdbcDataSynchronizationOperation.getTargetDataSourceLink(),
                    jdbcDataSynchronizationOperation.getTargetConn());
        }
        JdbcDataSyncThreadLocal.removeTableList();
    }
}
