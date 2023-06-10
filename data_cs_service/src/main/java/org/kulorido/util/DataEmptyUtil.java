package org.kulorido.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;

/**
 * @Author kulorido
 * @Date 2099/12/31 14:18
 * @Version 1.0
 */
@Slf4j
public class DataEmptyUtil {

    public static boolean isEmpty(Object pObj) {
        if (pObj == null) {
            return true;
        } else if (pObj == "") {
            return true;
        } else {
            if (pObj instanceof String) {
                return ((String) pObj).length() == 0;
            } else if (pObj instanceof Collection) {
                return ((Collection) pObj).size() == 0;
            } else if (pObj instanceof Map && ( (Map) pObj).size() == 0) {
                return true;
            } else  if (pObj instanceof Long){
                return (Long) pObj == 0L;
            } else if (pObj instanceof Integer){
                return (Integer) pObj == 0;
            }

            return false;
        }
    }

    /**
     * 判断对象是否为NotEmpty(!null或元素>0)<br>
     * 实用于对如下对象做判断:String Collection及其子类 Map及其子类
     *
     * @param pObj
     *            待检查对象
     * @return boolean 返回的布尔值
     */
    public static boolean isNotEmpty(Object pObj) {
        if (pObj == null){
            return false;
        }
        if (pObj == ""){
            return false;
        }
        if ("null".equals(pObj)){
            return false;
        }
        if ("EMPTY".equals(pObj)){
            return false;
        }
        if (pObj instanceof Long){
            if ((Long) pObj == 0L){
                return false;
            }
        }
        if (pObj instanceof Integer){
            if ((Integer) pObj == 0){
                return false;
            }
        }
        if (pObj instanceof String) {
            return ((String) pObj).length() != 0;
        } else if (pObj instanceof Collection) {
            return ((Collection) pObj).size() != 0;
        } else if (pObj instanceof Map) {
            return ((Map) pObj).size() != 0;
        }
        return true;
    }
}
