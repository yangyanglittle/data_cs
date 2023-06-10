package org.kulorido.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @package org.kulorido.request
 * @Author kulorido
 * @Data 2023/6/9 13:16
 */
@Data
@ApiModel
public class SyncMysqlDataRequest {

    @ApiModelProperty(name = "configId",value = "配置ID")
    String configId;

    @ApiModelProperty(name = "tableName",value = "数据库表名(单个/多个逗号隔开)")
    String tableName;

    @ApiModelProperty(name = "size",value = "每次读写条数")
    Integer size;
}
