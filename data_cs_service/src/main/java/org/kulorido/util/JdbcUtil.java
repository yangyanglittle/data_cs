package org.kulorido.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.kulorido.model.TableDbInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 自定义的JDBC工具类
 * close(ResultSet rs,Statement stat,Connection conn) 释放资源方法
 * 		Statement处,也可以传入子类对象PreparedStatement
 * getConn()	用于注册驱动并获取连接 (无参, 连接默认数据库)
 * getConn()	重载方法...(IP地址, 端口, 数据库名, 用户名, 密码, 字符编码)
 */
@Slf4j
public class JdbcUtil {

    /**
     * 此方法是用于释放JDBC程序中的资源
     * @param rs	结果集对象
     * @param stat	传输器对象
     * @param conn	连接对象
     */
    public static void close(ResultSet rs,Statement stat,Connection conn) {
        //释放资源
        if (rs != null) {
            try {
                rs.close();
            }catch(Exception e){
                log.error("JDBC资源关闭异常",e);
            }finally {
                rs = null;
            }
        }
        if (stat != null) {
            try {
                stat.close();
            }catch(Exception e){
                log.error("JDBC资源关闭异常",e);
            }finally {
                stat = null;
            }
        }
        if (conn != null) {
            try {
                conn.close();
            }catch(Exception e){
                log.error("JDBC资源关闭异常",e);
            }finally {
                conn = null;
            }
        }
    }
    /**
     * 此方法是用于释放JDBC程序中的资源
     * @param stat	传输器对象
     * @param conn	连接对象
     */
    public static void close(Statement stat,Connection conn) {
        //释放资源
        if (stat != null) {
            try {
                stat.close();
            }catch(Exception e){
                log.error("JDBC资源关闭异常",e);
            }finally {
                stat = null;
            }
        }
        if (conn != null) {
            try {
                conn.close();
            }catch(Exception e){
                log.error("JDBC资源关闭异常",e);
            }finally {
                conn = null;
            }
        }
    }

    /**
     * 用于注册驱动并获取连接 (无参, 连接默认数据库)
     * @return conn	返回连接对象
     */
    public static Connection getConn() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/jt_db?characterEncoding=utf-8",
                    "root","root");
            return conn;
        } catch (Exception e) {
            log.error("获取JDBC连接异常",e);
        }
        return null;
    }
    /**
     * 注册驱动并获取连接方法重载 (数据库名, 用户名, 密码)
     * @param db	数据库名
     * @param user	用户名
     * @param pwd	密码
     * @return conn	返回连接对象
     */
    public static Connection getConn(String db,String user,String pwd) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/"+db+"?characterEncoding=utf-8",
                    user,pwd);
            return conn;
        } catch (Exception e) {
            log.error("获取JDBC连接异常",e);
        }
        return null;
    }
    /**
     * 注册驱动并获取连接方法重载 (IP地址, 端口, 数据库名, 用户名, 密码)
     * @param ip	IP地址
     * @param port	端口
     * @param db	数据库名
     * @param user	用户名
     * @param pwd	密码
     * @return conn	返回连接对象
     */
    public static Connection getConn(String ip,String port,String db,String user,String pwd) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:mysql://"+ip+":"+port+"/"+db+"?characterEncoding=utf-8",
                    user,pwd);
            return conn;
        } catch (Exception e) {
            log.error("获取JDBC连接异常",e);
        }
        return null;
    }

    /**
     * 注册驱动并获取连接方法重载 (IP地址, 端口, 数据库名, 用户名, 密码, 字符编码)
     * @param ip	IP地址
     * @param port	端口
     * @param db	数据库名
     * @param user	用户名
     * @param pwd	密码
     * @param ce	字符编码
     * @return conn	返回连接对象
     */
    public static Connection getConn(String ip,String port,String db,String user,String pwd,String ce) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:mysql://"+ip+":"+port+"/"+db+"?characterEncoding="+ce,
                    user,pwd);
            return conn;
        } catch (Exception e) {
            log.error("获取JDBC连接异常",e);
        }
        return null;
    }

    /**
     * 注册驱动并获取连接方法 (IP地址:端口, 数据库名, 用户名, 密码, 字符编码)
     * @param host	IP地址:端口
     * @param db	数据库名
     * @param user	用户名
     * @param pwd	密码
     * @param ce	字符编码
     * @param mysqlType	数据库类型 5-5.7 8-8.0
     * @return conn	返回连接对象
     */
    public static Connection getConn3(String host,String db,String user,String pwd,String ce,String mysqlType) {
        Connection conn = null;
        try {
            String mysqlDriver = null;
            if ("5".equals(mysqlType)) {
                mysqlDriver = "com.mysql.jdbc.Driver";
            }
            if ("8".equals(mysqlType)) {
                mysqlDriver = "com.mysql.cj.jdbc.Driver";
            }

            Class.forName(mysqlDriver);
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + host + "/" + db + "?characterEncoding=" + ce +
                            "&serverTimezone=Asia/Shanghai&useSSL=false",
                    user,pwd);
            return conn;
        } catch (Exception e) {
            log.error("获取JDBC连接异常",e);
        }
        return null;
    }

    /**
     * 注册驱动并获取连接方法 (IP地址:端口, 数据库名, 用户名, 密码, 字符编码)
     * @param host	IP地址:端口
     * @param db	数据库名
     * @param user	用户名
     * @param pwd	密码
     * @param ce	字符编码
     * @return conn	返回连接对象
     */
    public static Connection getConn2(String host,String db,String user,String pwd,String ce) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:mysql://"+host+"/"+db+"?characterEncoding="+ce+"&serverTimezone=Asia/" +
                            "Shanghai&useSSL=false&rewriteBatchedStatements=true",
                    user,pwd);
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 注册驱动并获取连接方法 (IP地址:端口, 数据库名, 用户名, 密码, 字符编码)
     * @return conn	返回连接对象
     */
    @SneakyThrows
    public static Connection getConn2(TableDbInfo tableDbInfo) {

        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(
                "jdbc:mysql://" + tableDbInfo.getDbHost() + "/" +
                        tableDbInfo.getDataBase() + "?characterEncoding=" + tableDbInfo.getCharSet() +
                        "&serverTimezone=Asia/Shanghai&useSSL=false&zeroDateTimeBehavior=round" +
                        "&rewriteBatchedStatements=true",
                tableDbInfo.getDbUsername(), tableDbInfo.getDbPassword());
    }



    /** 测试数据库连接 */
    public static boolean testDbConn(String host,String db,String user,String pwd,String ce){
        boolean flag = false;

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://"+host+"/"+db+"?characterEncoding="+ce+"&serverTimezone=Asia/" +
                            "Shanghai&useSSL=false&rewriteBatchedStatements=true",
                    user,pwd);

            if (conn != null) {
                flag = true;
            }

        } catch (Exception e) {
            log.error("数据库连接测试失败:主机[{}]-数据库[{}]-账号[{}]-密码[{}]-字符集[{}]", host, db, user, pwd, ce, e);
        }finally {
            close(null, conn);
        }

        return flag;
    }
}