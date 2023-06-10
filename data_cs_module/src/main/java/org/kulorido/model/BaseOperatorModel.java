package org.kulorido.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @package org.kulorido.model
 * @Author v_xueweidong
 * @Data 2023/6/9 13:54
 */
@Data
public class BaseAuthModel {

    /** 创建时间 */
    private Date createBy;

    /** 创建时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 创建时间 */
    private Date updateBy;

    /** 创建时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
