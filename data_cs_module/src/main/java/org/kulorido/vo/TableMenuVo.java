package org.kulorido.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TableMenuVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String pid;
    private String title;
    private String name;
    private String menuType;
    private Integer level;
    private Boolean checked;
    private List<TableMenuVo> children;
}
