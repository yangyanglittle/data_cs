package org.kulorido.util;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将对象序列化为JSON字符串
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
}
