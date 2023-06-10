package org.kulorido.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static org.kulorido.util.SqlUtil.checkIsStr;


/**
 * @Author kulorido
 * @Date 2099/12/31 18:10
 * @Version 1.0
 */
public class StringJoinerUtil {

    public static StringJoiner getStringJoiner(String readSql, List<String> colList, List<String> colTypeList,
                                               Statement originDataSourceLink) throws SQLException {
        StringJoiner stringJoiner = new StringJoiner(",");
        ResultSet resultSet = originDataSourceLink.executeQuery(readSql);
        try {
            List<String> dataList = new ArrayList<>();
            while (resultSet.next()){
                StringJoiner data1 = new StringJoiner(",","(",")");
                for (int k = 0; k < colList.size(); k++) {
                    String c = colList.get(k);
                    String d2 = resultSet.getString(c);
                    String type = colTypeList.get(k);
                    if (d2 == null || "NULL".equals(d2.toUpperCase())){
                        data1.add("0");
                    }else {
                        // 检测字段类型是否是字符串
                        boolean isStr = checkIsStr(type);
                        if (isStr){
                            data1.add("'"+ SqlUtil.escapeString(d2) +"'");
                        } else {
                            data1.add(d2);
                        }
                    }
                }
                dataList.add(data1.toString());
            }
            dataList.forEach(stringJoiner::add);
        } finally {
            JdbcUtil.close(resultSet, null, null);
        }
        return stringJoiner;
    }


}
