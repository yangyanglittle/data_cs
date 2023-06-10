package org.kulorido.common.cache;

import lombok.Data;
import org.kulorido.model.TableDbInfo;
import org.kulorido.vo.TableMenuVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author kulorido
 * @Date 2099/12/31 15:04
 * @Version 1.0
 */
@Data
public class TableCacheData {

    private static Map<String, List<TableMenuVo>> MENU_VO_MAP = new ConcurrentHashMap<>(16);

    private static Map<String, List<TableDbInfo>> TABLE_DB_CONFIG_MAP = new HashMap<>(16);

    public static void setMenuvoMap(Map<String, List<TableMenuVo>> menuVoMap){
        MENU_VO_MAP = menuVoMap;
    }

    public static Map<String, List<TableMenuVo>> getMenuVoMap(){
        return MENU_VO_MAP;
    }

    public static void setTableDbConfigMap(Map<String, List<TableDbInfo>> tableDbConfigMap){
        TABLE_DB_CONFIG_MAP = tableDbConfigMap;
    }

    public static Map<String, List<TableDbInfo>> getTableDbConfigMap(){
        return TABLE_DB_CONFIG_MAP;
    }
}
