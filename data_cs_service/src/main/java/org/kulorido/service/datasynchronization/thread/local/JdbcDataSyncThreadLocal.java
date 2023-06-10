package com.baidu.personalcode.crmdatads.service.datasynchronization.thread.local;

import com.baidu.personalcode.crmdatads.pojo.TableDbInfo;

import java.util.List;

/**
 * @Author v_xueweidong
 * @Date 2022/9/19 17:32
 * @Version 1.0
 */
public class JdbcDataSyncThreadLocal {

    private static ThreadLocal<List<TableDbInfo>> TABLE_LIST = new ThreadLocal<>();

    public static List<TableDbInfo> getTableList(){
        return TABLE_LIST.get();
    }

    public static void removeTableList(){
        TABLE_LIST.remove();
    }

    public static void setTableList(List<TableDbInfo> tableLists){
        TABLE_LIST.set(tableLists);
    }
}
