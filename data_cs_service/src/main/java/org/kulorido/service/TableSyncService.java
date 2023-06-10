package com.baidu.personalcode.crmdatads.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.personalcode.crmdatads.dao.TableDbInfoDao;
import com.baidu.personalcode.crmdatads.pojo.DBView;
import com.baidu.personalcode.crmdatads.pojo.SourceData;
import com.baidu.personalcode.crmdatads.pojo.TableDbInfo;
import com.baidu.personalcode.crmdatads.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * 同步新表到其他数据库
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TableSyncService {
    private static final Logger log = LoggerFactory.getLogger(TableSyncService.class);

    /** 源数据库 */
    private static final String SOURCE = "master.json";

    /** 目标数据库 */
    private static final String TARGET = "targetList.json";

    @Autowired
    private TableDbInfoDao tableDbInfoDao;
    @Autowired
    private TableDbInfoService tableDbInfoService;

    /**
     * [读取数据库配置方式]
     *
     * 从源数据库同步新表(单个/多个)到其他数据库
     */
    public int syncSomeNewTable(String configId, List<String> tableList){
        int i = 0;

        List<TableDbInfo> dbList = tableDbInfoService.queryByConfigId(configId);

        // 获取主服务器原表创建语句
        List<String> sqlList = getDBMasterTableCreate(dbList.get(0),tableList);
        if (sqlList == null || sqlList.size() < 1) return 0;

        // 获取需要同步表的数据库信息(去掉源数据库信息)
        dbList.remove(0);

        // 执行表创建同步
        for (TableDbInfo dbInfo : dbList) {
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = JdbcUtil.getConn2(dbInfo);
                for (String sql : sqlList) {
                    try {
                        ps = conn.prepareStatement(sql);
                        //ps这里不需要参数,所以不用设置参数
                        ps.execute();

                        i++;
                    }catch (Exception e){
                        log.error("执行新表创建SQL[{}]错误",sql,e);
                    }
                }
            } catch (Exception e) {
                log.error("执行同步新表创建语句错误2",e);
                //e.printStackTrace();
            }finally {
                JdbcUtil.close(ps, conn);
            }
        }

        return i;
    }


    /**
     * [读取数据库配置方式]
     *
     * 单表/多表(旧表)结构同步
     * */
    public int syncOldTable(String configId,List<String> tableList){
        int result = 0;

        List<TableDbInfo> dbList = tableDbInfoService.queryByConfigId(configId);
        if (dbList.size() < 2) {
            return 0;
        }

        // 获取源数据库
        TableDbInfo sourceDB = dbList.get(0);
        dbList.remove(0);

        // 获取源数据表信息
        SourceData source = new SourceData();
        source.initSomeTable(sourceDB, tableList);

        for (TableDbInfo dbInfo : dbList) {
            SourceData target = new SourceData();
            target.initSomeTable(dbInfo, tableList);

            CompareUnits units = new CompareUnits(source, target);
            units.compare();

            int size = 0;
            int r = 0;
            if (units.getChangeSql() != null && units.getChangeSql().size() > 0){
                size = units.getChangeSql().size();
                for (int i = 0; i < size; i++) {
                    log.info(units.getChangeSql().get(i));
                    r += SqlUtil.ddl(target.getStmt(), target.getConn(), units.getChangeSql().get(i));
                }
                if (r == size){
                    result++;
                }
            }

            log.info("数据库"+ dbInfo.getDbHost()+" - "+ dbInfo.getDataBase()+"执行完毕!"+"总条数:"+ size+"成功:"+r);

            target.close();
        }

        source.close();

        return result;
    }


    /**
     * [读取数据库配置方式]
     *
     * 从源数据库同步 [全部] 表结构到其他数据库
     * */
    public int syncAllTable(String configId){
        int result = 0;

        List<TableDbInfo> dbList = tableDbInfoService.queryByConfigId(configId);
        if (dbList.size() < 2) return 0;

        // 获取源数据库
        TableDbInfo sourceDB = dbList.get(0);
        dbList.remove(0);

        // 获取源数据表信息
        SourceData source = new SourceData();
        source.init(sourceDB);

        // 获取源数据库视图
        List<DBView> viewList = getMasterView(sourceDB);

        for (TableDbInfo dbInfo : dbList) {
            SourceData target = new SourceData();
            target.init(dbInfo);

            CompareUnits units = new CompareUnits(source, target);
            units.compare();

            // 同步表结构
            int size = units.getChangeSql().size();
            int r = 0;
            for (int i = 0; i < size; i++) {
                String sql = units.getChangeSql().get(i);
                log.info("表同步SQL: "+sql);
                r += SqlUtil.ddl(target.getStmt(), target.getConn(), sql);
            }
            if (r == size){
                result++;
            }

            // 同步视图
            for (DBView view : viewList) {
                String sql0 = "drop view if EXISTS "+view.getViewName()+";";
                SqlUtil.ddl(target.getStmt(), target.getConn(), sql0);

                String sql1 = "CREATE VIEW "+view.getViewName()+" AS " + view.getViewDefinition() + ";";
                SqlUtil.ddl(target.getStmt(), target.getConn(), sql1);
                log.info("视图同步SQL: "+sql0+sql1);
            }


            log.info("==============数据库"+ dbInfo.getDbHost()+" - "+ dbInfo.getDataBase()+"同步完毕!==============");
            log.info("================表同步总条数:"+ size +" 成功:"+r+" -- 视图条数: "+viewList.size()+"================");

            target.close();
        }

        source.close();

        return result;
    }





    /**
     * [读取JSON配置文件方式]
     *
     * 从源数据库同步(单个/多个)新表到其他数据库
     */
    public int createSomeTable(String tableName){
        if (StringUtils.isBlank(tableName)) return 0;
        int i = 0;

        List<String> tableList = Arrays.asList(tableName.split(","));

        // 获取主服务器原表创建语句
        List<String> sqlList = getJsonMasterTableCreate(tableList);
        if (sqlList == null || sqlList.size() < 1) return 0;

        // 获取需要同步表的数据库信息
        List<TableDbInfo> dbList = getTargetDbList();

        // 执行表创建同步
        for (TableDbInfo dbInfo : dbList) {
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = JdbcUtil.getConn2(dbInfo);
                for (String sql : sqlList) {
                    ps = conn.prepareStatement(sql);
                    //ps这里不需要参数,所以不用设置参数
                    ps.execute();

                    i++;
                }
            } catch (Exception e) {
                log.error("执行同步新表创建语句错误",e);
                //e.printStackTrace();
            }finally {
                JdbcUtil.close(ps, conn);
            }
        }

        return i;
    }


    /**
     * [读取JSON配置文件方式]
     *
     * 从源数据库同步单个/多个表(旧表)结构到其他数据库
     * */
    public int updateSomeTable(String tableName){
        if (StringUtils.isBlank(tableName)) return 0;

        List<String> tableList = Arrays.asList(tableName.split(","));

        int result = 0;
        // 获取源数据库
        TableDbInfo sourceDB = getSourceDB();

        // 获取源数据表信息
        SourceData source = new SourceData();
        source.initSomeTable(sourceDB,tableList);

        // 获取目标数据库列表
        List<TableDbInfo> dbList = getTargetDbList();
        for (TableDbInfo dbInfo : dbList) {
            SourceData target = new SourceData();
            target.initSomeTable(dbInfo,tableList);

            CompareUnits units = new CompareUnits(source, target);
            units.compare();

            int size = units.getChangeSql().size();
            int r = 0;
            for (int i = 0; i < size; i++) {
                log.info(units.getChangeSql().get(i));
                r += SqlUtil.ddl(target.getStmt(), target.getConn(), units.getChangeSql().get(i));
            }

            if (r == size){
                result++;
            }

            log.info("数据库"+ dbInfo.getDbHost()+" - "+ dbInfo.getDataBase()+"执行完毕!"+"总条数:"+ size+"成功:"+r);
        }

        return result;
    }


    /**
     * [读取JSON配置文件方式]
     *
     * 从源数据库同步 [全部] 表结构到其他数据库
     * */
    public int updateAllTable(){
        int result = 0;
        // 获取源数据库
        TableDbInfo sourceDB = getSourceDB();

        // 获取源数据表信息
        SourceData source = new SourceData();
        source.init(sourceDB);

        // 获取源数据库视图
        List<DBView> viewList = getMasterView(sourceDB);

        // 获取目标数据库列表
        List<TableDbInfo> dbList = getTargetDbList();
        for (TableDbInfo tableDbInfo : dbList) {
            SourceData target = new SourceData();
            target.init(tableDbInfo);

            CompareUnits units = new CompareUnits(source, target);
            units.compare();

            // 同步表结构
            int size = units.getChangeSql().size();
            int r = 0;
            for (int i = 0; i < size; i++) {
                String sql = units.getChangeSql().get(i);
                log.info("表同步SQL: "+sql);
                r += SqlUtil.ddl(target.getStmt(), target.getConn(), sql);
            }

            if (r == size){
                result++;
            }

            // 同步视图
            for (DBView view : viewList) {
                String sql0 = "drop view if EXISTS "+view.getViewName()+";";
                SqlUtil.ddl(target.getStmt(), target.getConn(), sql0);

                String sql1 = "CREATE VIEW "+view.getViewName()+" AS " + view.getViewDefinition() + ";";
                SqlUtil.ddl(target.getStmt(), target.getConn(), sql1);
                log.info("视图同步SQL: "+sql0+sql1);
            }

            log.info("==============数据库"+ tableDbInfo.getDbHost()+" - "+ tableDbInfo.getDataBase()+"同步完毕!==============");
            log.info("================表同步总条数:"+ size +" 成功:"+r+" -- 视图条数: "+viewList.size()+"================");

            target.close();
        }

        source.close();

        return result;
    }






    /**
     * [读取JSON文件方式]
     *
     * 获取主服务器原表创建语句
     * */
    public List<String> getJsonMasterTableCreate(List<String> tableList){
        TableDbInfo tableDbInfo = getSourceDB();
        if (tableDbInfo == null) return null;

        List<String> createSQL = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JdbcUtil.getConn2(tableDbInfo);
            for (String tableName : tableList) {
                String sql = "show create table "+ tableDbInfo.getDataBase()+"."+tableName;
                ps = conn.prepareStatement(sql);
                //ps这里不需要参数,所以不用设置参数
                rs = ps.executeQuery();
                while (rs.next()) {
                    String cs = rs.getString(2);
                    createSQL.add(cs);
                    log.info("=================================↓源数据库[{}]表create代码↓=================================",tableName);
                    log.info(cs);
                    log.info("=================================↑源数据库[{}]表create代码↑=================================",tableName);
                }
            }

        } catch (Exception e) {
            log.error("获取源数据库表create代码错误",e);
            //e.printStackTrace();
        }finally {
            JdbcUtil.close(rs, ps, conn);
        }

        return createSQL;
    }



    /**
     * [读取数据库方式]
     *
     * 获取主服务器原表创建语句
     * */
    public List<String> getDBMasterTableCreate(TableDbInfo dbInfo, List<String> tableList){
        List<String> createSQL = new ArrayList<>();

        if (dbInfo == null) return null;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JdbcUtil.getConn2(dbInfo);
            for (String tableName : tableList) {
                String sql = "show create table "+ dbInfo.getDataBase()+"."+tableName;
                ps = conn.prepareStatement(sql);
                //ps这里不需要参数,所以不用设置参数
                rs = ps.executeQuery();
                while (rs.next()) {
                    String cs = rs.getString(2);
                    createSQL.add(cs);
                    log.info("=================================↓源数据库[{}]表create代码↓=================================",tableName);
                    log.info(cs);
                    log.info("=================================↑源数据库[{}]表create代码↑=================================",tableName);
                }
            }

        } catch (Exception e) {
            log.error("获取源数据库表create代码错误",e);
            //e.printStackTrace();
        }finally {
            JdbcUtil.close(rs, ps, conn);
        }

        return createSQL;
    }



    /**
     * [JSON配置]
     * 获取源数据库信息
     * */
    public TableDbInfo getSourceDB(){
        TableDbInfo tableDbInfo = null;
        JSONObject jsonObject = null;
        try {
            jsonObject = JsonUtil.getJSONObject(SOURCE);
            tableDbInfo = new TableDbInfo();
            tableDbInfo.setDbHost(jsonObject.getString("host"));
            tableDbInfo.setDbUsername(jsonObject.getString("username"));
            tableDbInfo.setDbPassword(jsonObject.getString("password"));
            tableDbInfo.setDataBase(jsonObject.getString("database"));
            tableDbInfo.setMysqlType(jsonObject.getString("mysqlType"));
            tableDbInfo.setCharSet(jsonObject.getString("charSet"));
        } catch (Exception e) {
            log.info("解析源数据库信息JSON出错,数据路径[{}],解析内容[{}]",SOURCE,jsonObject);
        }

        return tableDbInfo;
    }



    /**
     * [JSON配置]
     * 获取需要同步表的数据库信息
     * */
    public List<TableDbInfo> getTargetDbList(){
        List<TableDbInfo> list = new ArrayList<>();
        JSONArray array = null;

        try {
            array = JsonUtil.getJSONArray(TARGET);
            for (Object o : array) {
                JSONObject jsonObject = (JSONObject)o;
                TableDbInfo tableDbInfo = new TableDbInfo();
                tableDbInfo.setDbHost(jsonObject.getString("host"));
                tableDbInfo.setDbUsername(jsonObject.getString("username"));
                tableDbInfo.setDbPassword(jsonObject.getString("password"));
                tableDbInfo.setDataBase(jsonObject.getString("database"));
                tableDbInfo.setMysqlType(jsonObject.getString("mysqlType"));
                tableDbInfo.setCharSet(jsonObject.getString("charSet"));
                list.add(tableDbInfo);
            }
        } catch (Exception e) {
            log.info("解析目标数据库JSON列表出错,数据路径[{}],解析内容[{}]",TARGET,array);
        }

        return list;
    }


    /** 获取源数据库视图 */
    public List<DBView> getMasterView(TableDbInfo tableDbInfo){
        List<DBView> list = new ArrayList<>();

        ResultSet rs = null;
        Statement stmt = null;
        Connection conn = null;
        try {
            String db = tableDbInfo.getDataBase();
            conn = JdbcUtil.getConn2(tableDbInfo);
            stmt = conn.createStatement();
            String sql = "SELECT TABLE_NAME,view_definition FROM information_schema.views WHERE table_schema = '"+db+"'";
            rs = SqlUtil.executeSql(rs, stmt, conn, sql);
            while (rs.next()) {
                DBView view = new DBView();
                view.setViewName(rs.getString("TABLE_NAME"));
                view.setViewDefinition(rs.getString("view_definition"));
                list.add(view);
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            JdbcUtil.close(rs,stmt,conn);
        }

        return list;
    }


    //查询源数据库所有视图名
    public List<String> queryAllView(String configId){
        List<String> viewList = new ArrayList<>();

        TableDbInfo dbInfo = tableDbInfoDao.queryMasterByConfigId(configId);
        if (dbInfo == null) return viewList;

        String host = dbInfo.getDbHost();
        String db = dbInfo.getDataBase();
        String user = dbInfo.getDbUsername();
        String pwd = JasyptEncryptUtils.decryptPwd(dbInfo.getDbPassword(), dbInfo.getDbSalt());
        String ce = dbInfo.getCharSet();

        ResultSet rs = null;
        Statement stmt = null;
        Connection conn = null;
        try {
            conn = JdbcUtil.getConn2(new TableDbInfo(host,db, user, pwd, ce));
            stmt = conn.createStatement();
            String querySql = "SELECT TABLE_NAME FROM information_schema.views WHERE table_schema = '"+db+"'";
            rs = SqlUtil.executeSql(rs, stmt, conn, querySql);
            while (rs.next()) {
                viewList.add(rs.getString("TABLE_NAME"));
            }
        } catch (Exception e) {
            log.error("获取源库视图出错",e);
        } finally {
            JdbcUtil.close(rs,stmt,conn);
        }

        return viewList;
    }

    //同步视图
    public int syncView(String configId, List<String> funNameList){

        int result = 0;

        List<TableDbInfo> dbList = tableDbInfoService.queryByConfigId(configId);
        if (dbList.size() < 2) return 0;

        // 获取源数据库函数列表
        List<String> viewSqlList = new ArrayList<>();

        StringJoiner nameStr = new StringJoiner(",","(",")");
        for (String s : funNameList) {
            nameStr.add("'"+s+"'");
        }

        TableDbInfo masDB = dbList.get(0);
        ResultSet mrs = null;
        Statement mstmt = null;
        Connection mconn = null;
        try {
            mconn = JdbcUtil.getConn2(masDB);
            mstmt = mconn.createStatement();
            String querySql = "SELECT TABLE_NAME,view_definition FROM information_schema.views WHERE table_schema = '"+masDB.getDataBase()+"' AND TABLE_NAME IN " + nameStr;
            mrs = SqlUtil.executeSql(mrs, mstmt, mconn, querySql);
            while (mrs.next()) {
                String vName = mrs.getString("TABLE_NAME");
                String dSql = "drop view if EXISTS "+ vName +";";
                viewSqlList.add(dSql);
                String definition = mrs.getString("view_definition");
                String vSql = "CREATE VIEW "+ vName +" AS " + definition + ";";
                viewSqlList.add(vSql);
                log.info("视图创建语句:{}",vSql);
            }
        } catch (Exception e) {
            log.error("获取源库视图出错",e);
        } finally {
            JdbcUtil.close(mrs,mstmt,mconn);
        }

        if (viewSqlList.size() < 1) return 0;

        // 同步操作
        for (int i = 1; i < dbList.size(); i++) {
            TableDbInfo tarDB = dbList.get(i);
            Statement tstmt = null;
            Connection tconn = null;
            try {
                tconn = JdbcUtil.getConn2(tarDB);
                tconn.setAutoCommit(false);
                tstmt = tconn.createStatement();
                for (String sql2 : viewSqlList) {
                    tstmt.execute(sql2);
                }
                tconn.commit();

                result++;
            } catch (Exception e) {
                log.error("同步[{}]视图出错",tarDB.getDbHost(),e);
            } finally {
                JdbcUtil.close(tstmt,tconn);
            }
        }

        return result;
    }



    //查询源数据库所有函数名
    public List<String> queryAllFunction(String configId){
        List<String> funList = new ArrayList<>();

        TableDbInfo dbInfo = tableDbInfoDao.queryMasterByConfigId(configId);
        if (dbInfo == null) return funList;

        String host = dbInfo.getDbHost();
        String db = dbInfo.getDataBase();
        String user = dbInfo.getDbUsername();
        String pwd = JasyptEncryptUtils.decryptPwd(dbInfo.getDbPassword(), dbInfo.getDbSalt());
        String ce = dbInfo.getCharSet();

        ResultSet rs = null;
        Statement stmt = null;
        Connection conn = null;
        try {
            conn = JdbcUtil.getConn2(new TableDbInfo(host,db, user, pwd, ce));
            stmt = conn.createStatement();
            String querySql = "SELECT name FROM mysql.proc WHERE db = '"+db+"' and type = 'FUNCTION'";
            rs = SqlUtil.executeSql(rs, stmt, conn, querySql);
            while (rs.next()) {
                funList.add(rs.getString("name"));
            }
        } catch (Exception e) {
            log.error("获取源库函数出错",e);
        } finally {
            JdbcUtil.close(rs,stmt,conn);
        }

        return funList;
    }


    //同步函数
    public int syncFunction(String configId, List<String> funNameList){
        //String querySql = "SELECT name,param_list,returns,body FROM mysql.proc WHERE db = 'gdtqbd' and type = 'FUNCTION'";
        //String querySql = "SELECT name,param_list,returns,body FROM mysql.proc WHERE db = 'gdtqbd' AND type = 'FUNCTION' AND name = 'queryChildID'";

        int result = 0;

        List<TableDbInfo> dbList = tableDbInfoService.queryByConfigId(configId);
        if (dbList.size() < 2) return 0;

        // 获取源数据库函数列表
        List<String> funSqlList = new ArrayList<>();

        StringJoiner nameStr = new StringJoiner(",","(",")");
        for (String s : funNameList) {
            nameStr.add("'"+s+"'");
        }

        TableDbInfo masDB = dbList.get(0);
        ResultSet mrs = null;
        Statement mstmt = null;
        Connection mconn = null;
        try {
            mconn = JdbcUtil.getConn2(masDB);
            mstmt = mconn.createStatement();
            String querySql = "SELECT name,param_list par,returns rt,body FROM mysql.proc WHERE db = '"+masDB.getDataBase()+"' AND type = 'FUNCTION' AND name IN " + nameStr;
            mrs = SqlUtil.executeSql(mrs, mstmt, mconn, querySql);
            while (mrs.next()) {
                String fName1 = mrs.getString("name");
                String sql1 = "drop function if exists "+fName1;
                funSqlList.add(sql1);
                String sql2 = "SET FOREIGN_KEY_CHECKS=0";
                funSqlList.add(sql2);

                String par1 = mrs.getString("par");
                String rt1 = mrs.getString("rt");
                String ret = rt1.substring(0,rt1.indexOf("CHARSET")-1);
                String body1 = mrs.getString("body");
                String sql3 = "create function " + fName1 + "("+par1+") " + "returns " + ret + " " + body1;
                funSqlList.add(sql3);
                log.info("函数创建语句:{}",sql3);

                String sql4 = "SET FOREIGN_KEY_CHECKS=1";
                funSqlList.add(sql4);
            }
        } catch (Exception e) {
            log.error("获取源库函数出错",e);
        } finally {
            JdbcUtil.close(mrs,mstmt,mconn);
        }

        if (funSqlList.size() < 1) return 0;

        // 同步操作
        for (int i = 1; i < dbList.size(); i++) {
            TableDbInfo tarDB = dbList.get(i);
            Statement tstmt = null;
            Connection tconn = null;
            try {
                tconn = JdbcUtil.getConn2(tarDB);
                tconn.setAutoCommit(false);
                tstmt = tconn.createStatement();
                for (String sql2 : funSqlList) {
                    tstmt.execute(sql2);
                }
                tconn.commit();

                result++;
            } catch (Exception e) {
                log.error("同步[{}]函数出错",tarDB.getDbHost(),e);
            } finally {
                JdbcUtil.close(tstmt,tconn);
            }
        }

        return result;
    }



    /** 数据库配置ID获取源数据库所有表 */
    public List<String> getMasterTabList(String configId){
        List<String> tabList = new ArrayList<>();
        TableDbInfo dbInfo = tableDbInfoDao.queryMasterByConfigId(configId);
        if (dbInfo != null){
            String host = dbInfo.getDbHost();
            String db = dbInfo.getDataBase();
            String user = dbInfo.getDbUsername();
            String pwd = JasyptEncryptUtils.decryptPwd(dbInfo.getDbPassword(), dbInfo.getDbSalt());
            String ce = dbInfo.getCharSet();

            boolean flag = JdbcUtil.testDbConn(host, db, user, pwd, ce);

            if (!flag){
                throw new IllegalArgumentException("数据库信息有误,连接失败");
            }

            ResultSet rs = null;
            Statement stmt = null;
            Connection conn = null;
            try {
                conn = DriverManager.getConnection(
                        "jdbc:mysql://"+host+"/"+db+"?characterEncoding="+ce+"&serverTimezone=Asia/Shanghai&useSSL=false",
                        user,pwd);
                stmt = conn.createStatement();
                String sql = "select TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = '"+db+"' AND TABLE_TYPE = 'BASE TABLE'";
                rs = SqlUtil.executeSql(rs,stmt,conn, sql);

                while (rs.next()) {
                    tabList.add(rs.getString("TABLE_NAME"));
                }

            } catch (Exception e) {
                log.info("数据库表-连接失败:主机[{}]-数据库[{}]-账号[{}]-密码[{}]-字符集[{}]",host,db,user,pwd,ce);
            } finally {
                JdbcUtil.close(rs, stmt, conn);
            }
        }else {
            throw new IllegalArgumentException("未获取到源数据库信息");
        }

        return tabList;
    }


    public static void main(String[] args) {
        String s = "varchar(1000) CHARSET utf8";
        System.out.println(s.length());
        System.out.println(s.indexOf("CHARSET"));
        System.out.println(s.substring(0,13));
    }

}
