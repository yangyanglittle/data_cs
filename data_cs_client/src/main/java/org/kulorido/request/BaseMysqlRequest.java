package org.kulorido.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @package org.kulorido.request
 * @Author kulorido
 * @Data 2023/6/9 13:39
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseMysqlRequest extends BaseOperatorRequest{

    private String configId;

//    private String createBy;
//
//    private String updateBy;
//
//    private Date createTime;
//
//    private Date updateTime;
}
