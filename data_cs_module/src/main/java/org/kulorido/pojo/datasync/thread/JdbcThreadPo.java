package org.kulorido.pojo.datasync.thread;

import lombok.Builder;
import lombok.Data;
import org.kulorido.pojo.datasync.JdbcDataSynchronizationOperation;

import java.sql.Statement;
import java.util.List;
import java.util.StringJoiner;

/**
 * @Author kulorido
 * @Version 1.0
 */
@Data
@Builder
public class JdbcThreadPo {

    private int num;

    private StringJoiner columns1;

    private List<String> colList;

    private List<String> colTypeList;

    private JdbcDataSynchronizationOperation jdbcDataSynchronizationOperation;

    private Statement originDataSourceLink;

    private Statement targetDataSourceLink;

    private String tab;

    private int size;

    private boolean queue;

    private String configId;

    private Integer totalCount;
}
