package com.jin10.spider.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @author zxlei
 * @date 2019/8/29 10:22
 * ----------------------------------------------
 * TODO
 * ----------------------------------------------
 */
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;


    }

    /**
     * json转对象
     * @param string
     * @param classType
     * @return
     */
    public static Object fromJson(String string, Class classType) {
        try {
            return objectMapper.readValue(string, classType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * json转对象
     * @param string
     * @param typeReference
     * @return
     */
    public static Object fromJson(String string, TypeReference typeReference) {
        try {
            return objectMapper.readValue(string, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
