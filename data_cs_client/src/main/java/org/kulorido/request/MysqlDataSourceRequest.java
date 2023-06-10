package org.kulorido.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @package org.kulorido.request
 * @Author kulorido
 * @Data 2023/6/9 13:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MysqlDataSourceRequest extends MysqlConfigRequest{

    @ApiModelProperty("数据库类型 1源数据库 2目标数据库")
    @NotNull(message = "数据库类型不可为空 1源数据库 2目标数据库")
    private Integer dbType;

    @ApiModelProperty("主机IP端口")
    @NotEmpty(message = "主机IP端口不可为空")
    private String dbHost;

    @ApiModelProperty("主机用户名")
    @NotEmpty(message = "主机用户名不可为空")
    private String dbUsername;

    @ApiModelProperty("主机密码")
    @NotEmpty(message = "主机密码不可为空")
    private String dbPassword;

    @ApiModelProperty("数据库名")
    @NotEmpty(message = "数据库名不可为空")
    private String dataBase;

    @ApiModelProperty("数据库类型")
    @NotEmpty(message = "数据库类型不可为空")
    private String mysqlType;

    @ApiModelProperty("字符集 utf8")
    @NotEmpty(message = "字符集不可为空")
    private String charSet;
}
