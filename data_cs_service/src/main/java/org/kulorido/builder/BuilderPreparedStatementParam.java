package org.kulorido.builder;

import lombok.extern.slf4j.Slf4j;
import org.kulorido.pojo.datasync.JdbcDataSynchronizationOperation;
import org.kulorido.util.JdbcUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.StringJoiner;

import static org.kulorido.util.SqlUtil.checkIsStr;

/**
 * @Author kulorido
 * @Date 2099/12/31 19:44
 * @Version 1.0
 */
@Component
@Slf4j
public class BuilderPreparedStatementParam {

    public void setPreparedStatementParam(String readSql, List<String> colList, List<String> colTypeList,
                                          JdbcDataSynchronizationOperation jdbcDataSynchronizationOperation,
                                          StringBuilder placeholderStr, StringJoiner columns1) throws SQLException {
        Connection targetThreadConnection = JdbcUtil.getConn2(jdbcDataSynchronizationOperation.getTd());
        Connection originThreadConnection = JdbcUtil.getConn2(jdbcDataSynchronizationOperation.getMd());

        targetThreadConnection.setAutoCommit(false);
        PreparedStatement statement = targetThreadConnection.prepareStatement(
                "INSERT INTO "+ jdbcDataSynchronizationOperation.getTableName() + "("+columns1+")" +
                        "VALUES( " + placeholderStr + " )");

        Statement originDataSourceLink = originThreadConnection.createStatement();
        ResultSet resultSet = originDataSourceLink.executeQuery(readSql);
        try {
            while (resultSet.next()){
                for (int k = 0; k < colList.size(); k++) {
                    String c = colList.get(k);
                    String d2 = resultSet.getString(c);
                    String type = colTypeList.get(k);
                    if (d2 == null || "NULL".equalsIgnoreCase(d2)){
                        statement.setString(k + 1, "NULL");
                    }else {
                        boolean isStr = checkIsStr(type);
                        if (isStr){
                            statement.setString(k + 1, d2);
                        } else if ("INT".equals(type) || "TINYINT".equals(type) || "BIGINT".equals(type)){
                            statement.setInt(k + 1, Integer.parseInt(d2));
                        } else if ("FLOAT".equals(type) || "DOUBLE".equals(type) || "DECIMAL".equals(type)){
                            statement.setBigDecimal(k + 1, new BigDecimal(d2));
                        }
                    }
                }
                statement.addBatch();
            }
            statement.executeBatch();
            targetThreadConnection.commit();
            statement.clearBatch();
            log.info("处理完第:{}  批SQL数据了", readSql);
        } finally {
            JdbcUtil.close(resultSet, originDataSourceLink, null);
            JdbcUtil.close(null, statement, targetThreadConnection);
        }
    }
}
