package org.kulorido.service.datasynchronization.invoke;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import lombok.extern.slf4j.Slf4j;
import org.kulorido.exception.DataSynchronizationDataSourceException;
import org.kulorido.model.TableDbInfo;
import org.kulorido.pojo.datasync.DataSynchronizationPoBase;
import org.kulorido.pojo.datasync.JdbcDataSynchronizationOperation;
import org.kulorido.pojo.datasync.JdbcDataSynchronizationPo;
import org.kulorido.pojo.datasync.thread.DataThreadPo;
import org.kulorido.pojo.datasync.thread.JdbcThreadPo;
import org.kulorido.service.MysqlTableConfigService;
import org.kulorido.service.datasynchronization.invoke.base.JdbcDataSynchronizationBase;
import org.kulorido.service.datasynchronization.thread.DataThreadService;
import org.kulorido.service.datasynchronization.thread.local.JdbcDataSyncThreadLocal;
import org.kulorido.util.DataEmptyUtil;
import org.kulorido.util.DataSynchronizationJudge;
import org.kulorido.util.JdbcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static org.kulorido.common.constants.ResponseConstants.RES_MSG_DATA_NULL_BASIC;
import static org.kulorido.common.constants.ResponseConstants.RES_MSG_NULL_BASIC;


/**
 * @Author kulorido
 * @Date 2099/12/31 14:13
 * @Version 1.0
 */
@Service
@Slf4j
public class JdbcDataSynchronizationInvoke extends JdbcDataSynchronizationBase {

    @Autowired
    private MysqlTableConfigService tableDbInfoService;

    @Autowired
    private DataThreadService dataThreadService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Override
    protected void dataSynchronizationPreCheck(DataSynchronizationPoBase dataSynchronizationPoBase) {
        super.dataSynchronizationPreCheck(dataSynchronizationPoBase);

        JdbcDataSynchronizationPo jdbcDataSynchronizationPo = dataSynchronizationPoBase.getJdbcDataSynchronizationPo();
        DataSynchronizationJudge.isNull(jdbcDataSynchronizationPo, RES_MSG_NULL_BASIC);
    }

    @Override
    protected void dataSynchronizationBeforePostProcess(DataSynchronizationPoBase dataSynchronizationPoBase) throws SQLException {

        log.info("dynamicRoutingDataSource :{}", dynamicRoutingDataSource.determineDataSource());

        List<TableDbInfo> tableDbInfos = tableDbInfoService.queryByConfigId(dataSynchronizationPoBase.getConfigId());

        DataSynchronizationJudge.isNull(tableDbInfos, RES_MSG_DATA_NULL_BASIC);

        if (tableDbInfos.size() != 2){
            throw new DataSynchronizationDataSourceException("数据库配置错误，一个config只允许出现两个DB配置");
        }
        Map<Integer, List<TableDbInfo>> dbTypeMaps = tableDbInfos.stream().collect(Collectors.groupingBy(
                TableDbInfo::getDbType));

        TableDbInfo md = dbTypeMaps.get(1).get(0);
        TableDbInfo td = dbTypeMaps.get(2).get(0);
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
