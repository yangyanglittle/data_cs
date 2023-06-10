package org.kulorido.util;

import com.baidu.personalcode.crmdatads.util.reflect.ReflectUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author v_xueweidong
 * @Date 2022/9/16 14:18
 * @Version 1.0
 */
@Slf4j
public class DataEmptyUtil {

    /**
     * 入参只要有一个为null，就返回true
     * 实体放前面，参数放后面，避免npe
     * @param pObj
     * @return
     */
    public static boolean isAnyEmpty(Object... pObj) {
        for (Object item : pObj){
            if (isEmpty(item)){
                return true;
            }
        }
        return false;
    }

    /**
     * 通过反射判断入参只要有一个为null，就返回true
     * @param pObj
     * @return
     */
    public static boolean isAnyEmpty(Object pObj) {
        if (isEmpty(pObj)){
            return true;
        }
        List<Field> fieldList = ReflectUtils.getAllField(pObj);
        for (Field item : fieldList){
            item.setAccessible(true);
            Object value = null;
            try {
                value = item.get(pObj);
                if (isEmpty(value)){
                    return true;
                }
            } catch (IllegalAccessException e) {
                log.error("ReflectUtils isAnyNotEmpty error", e);
                return true;
            }
        }
        return false;
    }

    public static boolean isEmpty(Object pObj) {
        if (pObj == null) {
            return true;
        } else if (pObj == "") {
            return true;
        } else {
            if (pObj instanceof String) {
                if (( (String) pObj).length() == 0) {
                    return true;
                }
            } else if (pObj instanceof Collection) {
                if (( (Collection) pObj).size() == 0) {
                    return true;
                }
            } else if (pObj instanceof Map && ( (Map) pObj).size() == 0) {
                return true;
            } else  if (pObj instanceof Long){
                if ((Long) pObj == 0L){
                    return true;
                }
            } else if (pObj instanceof Integer){
                if ((Integer) pObj == 0){
                    return true;
                }
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
            if (((String) pObj).length() == 0) {
                return false;
            }
        } else if (pObj instanceof Collection) {
            if (((Collection) pObj).size() == 0) {
                return false;
            }
        } else if (pObj instanceof Map) {
            if (((Map) pObj).size() == 0) {
                return false;
            }
        }
        return true;
    }
}
