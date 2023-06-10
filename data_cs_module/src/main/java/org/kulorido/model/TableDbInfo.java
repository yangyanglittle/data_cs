package org.kulorido.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(description="数据库信息")
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
    @ApiModelProperty(name="主键",value="主键")
    private String id;
    
    /** 同步配置ID */
    @ApiModelProperty(name="外键配置表ID", value="外键配置表ID")
    private String configId;
    
    /** 数据库类型 1源数据库 2目标数据库 */
    @ApiModelProperty(name="数据库类型 1源数据库 2目标数据库",value="数据库类型 1源数据库 2目标数据库")
    private String dbType;
    
    /** 数据库序号 */
    @ApiModelProperty(name="数据库序号",value="数据库序号")
    private String dbSort;
    
    /** 主机IP端口 */
    @ApiModelProperty(name="主机IP端口",value="主机IP端口")
    private String dbHost;
    
    /** 主机用户名 */
    @ApiModelProperty(name="主机用户名",value="主机用户名")
    private String dbUsername;
    
    /** 主机密码 */
    @ApiModelProperty(name="主机密码",value="主机密码")
    private String dbPassword;
    
    /** 生成秘钥的公钥 */
    @ApiModelProperty(name="生成秘钥的公钥",value="生成秘钥的公钥")
    private String dbSalt;
    
    /** 数据库名 */
    @ApiModelProperty(name="数据库名",value="数据库名")
    private String dataBase;
    
    /** 数据库类型 5-Mysql5  8-Mysql8 */
    @ApiModelProperty(name="数据库类型 5-Mysql5  8-Mysql8",value="数据库类型 5-Mysql5  8-Mysql8")
    private String mysqlType;
    
    /** 字符集 utf8 */
    @ApiModelProperty(name="字符集 utf8",value="字符集 utf8")
    private String charSet;

}