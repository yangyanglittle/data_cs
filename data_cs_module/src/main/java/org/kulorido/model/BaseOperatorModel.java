package org.kulorido.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @package org.kulorido.model
 * @Author kulorido
 * @Data 2023/6/9 13:54
 */
@Data
public class BaseOperatorModel {

    /** 创建时间 */
    private String createBy;

    /** 创建时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 创建时间 */
    private String updateBy;

    /** 创建时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
