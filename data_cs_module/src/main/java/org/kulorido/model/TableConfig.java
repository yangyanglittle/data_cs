package org.kulorido.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class TableConfig extends BaseOperatorModel implements Serializable {

   private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer autoId;

    /** 主键 */
    private String id;
    
    /** 配置名称 */
    private String name;
    
    /** 备注 */
    private String remark;
    
    /** 0否 1是 */
    private String isDelete;

    /** 配置类型 1结构同步 2数据同步 */
    private String configType;

}