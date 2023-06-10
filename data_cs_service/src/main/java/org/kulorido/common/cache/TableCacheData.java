package com.baidu.personalcode.crmdatads.common.cache;

import com.baidu.personalcode.crmdatads.pojo.TableDbInfo;
import com.baidu.personalcode.crmdatads.vo.MenuVo;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author v_xueweidong
 * @Date 2022/9/20 15:04
 * @Version 1.0
 */
@Data
public class TableCacheData {

    private static Map<String, List<MenuVo>> MENU_VO_MAP = new ConcurrentHashMap<>(16);

    private static Map<String, List<TableDbInfo>> TABLE_DB_CONFIG_MAP = new HashMap<>(16);

    public static void setMenuvoMap(Map<String, List<MenuVo>> menuVoMap){
        MENU_VO_MAP = menuVoMap;
    }

    public static Map<String, List<MenuVo>> getMenuVoMap(){
        return MENU_VO_MAP;
    }

    public static void setTableDbConfigMap(Map<String, List<TableDbInfo>> tableDbConfigMap){
        TABLE_DB_CONFIG_MAP = tableDbConfigMap;
    }

    public static Map<String, List<TableDbInfo>> getTableDbConfigMap(){
        return TABLE_DB_CONFIG_MAP;
    }
}
