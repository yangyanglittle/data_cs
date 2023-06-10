package com.baidu.personalcode.crmdatads.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.personalcode.crmdatads.DataSyncApplication;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;

@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    /** 读取JSON文件转换为字符串 */
    public static String readJSONStr(String filePath){
        String str = null;
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            InputStream is = resource.getInputStream();
            str = getInputStreamStr(is);
        } catch (IOException e) {
            log.error("Json转换出错",e);
        }

        return str;
    }


    /** 读取JSON文件转换为JSONObject */
    public static JSONObject getJSONObject(String filePath) {
        String str = null;
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            InputStream is = resource.getInputStream();
            str = getInputStreamStr(is);
        } catch (IOException e) {
            log.error("Json转换出错",e);
        }

        return JSONObject.parseObject(str);
    }


    /** 读取JSON文件转换为JSONArray */
    public static JSONArray getJSONArray(String filePath) {
        String str = null;
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            InputStream is = resource.getInputStream();
            str = getInputStreamStr(is);
        } catch (IOException e) {
            log.error("Json转换出错",e);
        }

        return JSONArray.parseArray(str);
    }


    /** 读取InputStream转换为字符串 */
    public static String getInputStreamStr(InputStream is){
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder s = new StringBuilder();
        String line;
        try {
            while ((line=br.readLine()) != null){
                s.append(line);
            }

        }catch (Exception e){
            log.error("Json转换出错",e);
        }finally {
            try {
                br.close();
            } catch (IOException e) {
                log.error("IO流关闭出错",e);
            }
            try {
                is.close();
            } catch (IOException e) {
                log.error("IO流关闭出错",e);
            }
        }

        return s.toString();
    }



    /**
     * 获取项目根路径
     */
    public static String getProjectRootPath() {
        String result = null;
        try {
            result = new File(DataSyncApplication.class.getResource("/").toURI().getPath()).getParentFile()
                    .getParentFile().getCanonicalPath();
        } catch (URISyntaxException e) {
            log.error("获取路径出错",e);
        } catch (IOException e) {
            log.error("获取路径IO出错",e);
        }

        if (result != null && !"".equals(result.trim())) {
            return result;
        } else {
            throw new RuntimeException("未找到项目根路径");
        }
    }

    /**
     * 将对象序列化为JSON字符串
     *
     * @param object
     * @return JSON字符串
     */
    public static String serialize(Object object) {
        Writer write = new StringWriter();
        try {
            objectMapper.writeValue(write, object);
        } catch (JsonGenerationException e) {
            log.error("JsonGenerationException when serialize object to json", e);
        } catch (JsonMappingException e) {
            log.error("JsonMappingException when serialize object to json", e);
        } catch (IOException e) {
            log.error("IOException when serialize object to json", e);
        }
        return write.toString();
    }

    /**
     * 将JSON字符串反序列化为对象
     *
     * @return 指定对象
     */
    public static <T> T deserialize(String json, Class<T> clazz) {
        Object object = null;
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            object = objectMapper.readValue(json, TypeFactory.rawClass(clazz));
        } catch (JsonParseException e) {
            log.error("JsonParseException when serialize object to json", e);
        } catch (JsonMappingException e) {
            log.error("JsonMappingException when serialize object to json", e);
        } catch (IOException e) {
            log.error("IOException when serialize object to json", e);
        }
        return (T) object;
    }

    /**
     * 将JSON字符串反序列化为对象
     *
     * @return 指定对象
     */
    public static <T> T deserialize(String json, TypeReference<T> typeRef) {
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return (T) objectMapper.readValue(json, typeRef);
        } catch (JsonParseException e) {
            log.error("JsonParseException when deserialize json", e);
        } catch (JsonMappingException e) {
            log.error("JsonMappingException when deserialize json", e);
        } catch (IOException e) {
            log.error("IOException when deserialize json", e);
        }
        return null;
    }

    /**
     * 将JSON字符串反序列化为对象
     *
     * @return 指定对象
     */
    public static <T> T deserializeIgnoreException(String json, Class<T> clazz) {
        Object object = null;
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            object = objectMapper.readValue(json, TypeFactory.rawClass(clazz));
        } catch (JsonParseException e) {
            log.error("JsonParseException when serialize object to json", e);
        } catch (JsonMappingException e) {
            log.warn("上游php返回结果错误，data:{}---->data:[],所以忽略这个错误");
        } catch (IOException e) {
            log.error("IOException when serialize object to json", e);
        }
        return (T) object;
    }
}
