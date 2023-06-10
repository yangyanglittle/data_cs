package org.kulorido.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @package org.kulorido.request
 * @Author kulorido
 * @Data 2023/6/9 13:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MysqlConfigRequest extends BaseMysqlRequest{

    @ApiModelProperty("配置名字")
    private String name;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("配置类型")
    private String configType;
}
