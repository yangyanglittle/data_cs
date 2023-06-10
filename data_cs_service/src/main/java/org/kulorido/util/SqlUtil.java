package org.kulorido.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlUtil {

    private static final Logger log = LoggerFactory.getLogger(SqlUtil.class);

    public static ResultSet executeSql(ResultSet rs, Statement stmt, Connection conn, String sql) throws SQLException {
        //Statement stmt = conn.createStatement();
        stmt.setQueryTimeout(20);
        rs = stmt.executeQuery(sql);
        //ResultSet rs = stmt.executeQuery(sql);
        return rs;
    }

    public static int ddl(Statement stmt, Connection conn, String sql) {
        int r = 0;
        try {
            //Statement stmt = conn.createStatement();
            stmt.setQueryTimeout(200);
            stmt.execute(sql);
            r = 1;
        } catch (Exception e) {
            log.info("ddl命令执行失败-[{}]", e.getMessage(), e);
        }

        return r;
    }

    public static String getDbString(String s) {
        return "'" + s + "'";
    }

    public static String escapeString(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        if ("\\".equals(s)){
            return "";
        }
        if (s.contains("'")){
            return s.replace("'", "\\'");
        }
        return s.replaceAll("'", "\\'").replace("\"", "\\\"");
    }

    public static boolean checkIsStr(String type){
        boolean isStr = true;

        if ("INT".equals(type) || "TINYINT".equals(type) || "BIGINT".equals(type)
                || "FLOAT".equals(type) || "DOUBLE".equals(type) || "DECIMAL".equals(type)){
            isStr = false;
        }

        return isStr;
    }
}
