package org.kulorido.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class TableDbInfo implements Serializable {

    public TableDbInfo(){}

    public TableDbInfo(String dbHost, String dataBase, String dbUsername, String dbPassword, String charSet){
        this.dbHost = dbHost;
        this.dataBase = dataBase;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.charSet = charSet;
    }

    public TableDbInfo(String configId){
        this.configId = configId;
    }

   private static final long serialVersionUID = 1L;

    /** 主键 */
    private String id;
    
    /** 同步配置ID */
    private String configId;
    
    /** 数据库类型 1源数据库 2目标数据库 */
    private Integer dbType;
    
    /** 主机IP端口 */
    private String dbHost;
    
    /** 主机用户名 */
    private String dbUsername;
    
    /** 主机密码 */
    private String dbPassword;
    
    /** 数据库名 */
    private String dataBase;
    
    /** 数据库类型 5-Mysql5  8-Mysql8 */
    private String mysqlType;
    
    /** 字符集 utf8 */
    private String charSet;

}