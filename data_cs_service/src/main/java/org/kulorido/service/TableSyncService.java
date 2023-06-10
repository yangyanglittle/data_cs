package org.kulorido.service;

import org.kulorido.mapper.TableDbInfoMapper;
import org.kulorido.model.TableDbInfo;
import org.kulorido.util.DataSynchronizationJudge;
import org.kulorido.util.JdbcUtil;
import org.kulorido.util.SqlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 同步新表到其他数据库
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TableSyncService {
    private static final Logger log = LoggerFactory.getLogger(TableSyncService.class);

    @Autowired
    private TableDbInfoMapper tableDbInfoDao;


    /** 数据库配置ID获取源数据库所有表 */
    public List<String> getMasterTabList(String configId) {
        List<String> tabList = new ArrayList<>();
        List<TableDbInfo> dbInfoList = tableDbInfoDao.queryByConfigId(configId, 1);
        if (CollectionUtils.isEmpty(dbInfoList)){
            return new ArrayList<>();
        }
        TableDbInfo dbInfo = dbInfoList.get(0);
        if (dbInfo != null) {
            String host = dbInfo.getDbHost();
            String db = dbInfo.getDataBase();
            String user = dbInfo.getDbUsername();
            String pwd = dbInfo.getDbPassword();
            String ce = dbInfo.getCharSet();

            boolean flag = JdbcUtil.testDbConn(host, db, user, pwd, ce);

            if (!flag) {
                throw new IllegalArgumentException("数据库信息有误,连接失败");
            }

            ResultSet rs = null;
            Statement stmt = null;
            Connection conn = null;
            try {
                conn = DriverManager.getConnection(
                        "jdbc:mysql://" + host + "/" + db + "?characterEncoding=" + ce +
                                "&serverTimezone=Asia/Shanghai&useSSL=false",
                        user, pwd);
                stmt = conn.createStatement();
                String sql = "select TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = '" + db +
                        "' AND TABLE_TYPE = 'BASE TABLE'";
                rs = SqlUtil.executeSql(rs, stmt, conn, sql);
                while (rs.next()) {
                    tabList.add(rs.getString("TABLE_NAME"));
                }
            } catch (Exception e) {
                log.info("数据库表-连接失败:主机[{}]-数据库[{}]-账号[{}]-密码[{}]-字符集[{}]", host, db, user, pwd, ce);
            } finally {
                JdbcUtil.close(rs, stmt, conn);
            }
        } else {
            throw new IllegalArgumentException("未获取到源数据库信息");
        }
        return tabList;
    }
}
