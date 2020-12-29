package com.jin10.spider.common.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Airey
 * @date 2020/1/17 16:30
 * ----------------------------------------------
 * 枚举工具类
 * ----------------------------------------------
 */
public class EnumUtils {

    /**
     * 根据反射，通过方法名称获取方法值，忽略大小写的
     *
     * @param methodName
     * @param obj
     * @param args
     * @return return value
     */
    private static <T> Object getMethodValue(String methodName, T obj,
                                             Object... args) {
        Object resut = "";

        try {
            /********************************* start *****************************************/
            //获取方法数组，这里只要共有的方法
            Method[] methods = obj.getClass().getMethods();
            if (methods.length <= 0) {
                return resut;
            }
            Method method = null;
            for (int i = 0, len = methods.length; i < len; i++) {
                if (methods[i].getName().equalsIgnoreCase(methodName)) {
                    //忽略大小写取方法
                    // isHas = true;
                    //如果存在，则取出正确的方法名称
                    methodName = methods[i].getName();
                    method = methods[i];
                    break;
                }
            }

            if (method == null) {
                return resut;
            }
            //方法执行
            resut = method.invoke(obj, args);
            if (resut == null) {
                resut = "";
            }
            return resut;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resut;
    }


    /**
     * 获取枚举类Map
     * @param enumT
     * @param methodNames
     * @param <T>
     * @return
     */
    public static <T> Map<Object, String> EnumToMap(Class<T> enumT,
                                                    String... methodNames) {
        Map<Object, String> enummap = new HashMap<Object, String>();
        if (!enumT.isEnum()) {
            return enummap;
        }
        T[] enums = enumT.getEnumConstants();
        if (enums == null || enums.length <= 0) {
            return enummap;
        }
        int count = methodNames.length;
        //默认接口value方法
        String valueMathod = "getCode";
        //默认接口description方法
        String desMathod = "getMessage";
        //扩展方法
        if (count >= 1 && !"".equals(methodNames[0])) {
            valueMathod = methodNames[0];
        }
        if (count == 2 && !"".equals(methodNames[1])) {
            desMathod = methodNames[1];
        }
        for (int i = 0, len = enums.length; i < len; i++) {
            T tobj = enums[i];
            try {
                //获取value值
                Object resultValue = getMethodValue(valueMathod, tobj);
                if ("".equals(resultValue)) {
                    continue;
                }
                //获取description描述值
                Object resultDes = getMethodValue(desMathod, tobj);
                //如果描述不存在获取属性值
                if ("".equals(resultDes)) {
                    resultDes = tobj;
                }
                enummap.put(resultValue, resultDes + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return enummap;
    }


}
