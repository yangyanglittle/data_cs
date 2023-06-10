package org.kulorido.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class TableConfig implements Serializable {

   private static final long serialVersionUID = 1L;

    /** 主键 */
    @ApiModelProperty(name="自增ID", value="自增")
    private Integer autoId;

    /** 主键 */
    @ApiModelProperty(name="主键", value="主键")
    private String id;
    
    /** 配置名称 */
    @ApiModelProperty(name="配置名称", value="配置名称")
    private String name;
    
    /** 备注 */
    @ApiModelProperty(name="备注", value="备注")
    private String remark;
    
    /** 创建时间 */
    @ApiModelProperty(name="创建时间", value="创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    
    /** 0否1是 */
    @ApiModelProperty(name="是否删除 0否1是", value="是否删除 0否1是")
    private String isDelete;

    /** 配置类型 1结构同步 2数据同步 */
    @ApiModelProperty(name="配置类型 1结构同步 2数据同步", value="配置类型 1结构同步 2数据同步")
    private String configType;

}