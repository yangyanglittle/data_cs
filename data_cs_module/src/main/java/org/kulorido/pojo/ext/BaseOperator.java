package org.kulorido.pojo.ext;

import lombok.Data;

import java.util.Date;

/**
 * @package org.kulorido.pojo.ext
 * @Author kulorido
 * @Data 2023/6/9 13:59
 */
@Data
public class BaseOperator {

    /** 创建时间 */
    private Date createBy;

    /** 创建时间 */
    private Date createTime;

    /** 创建时间 */
    private Date updateBy;

    /** 创建时间 */
    private Date updateTime;
}
