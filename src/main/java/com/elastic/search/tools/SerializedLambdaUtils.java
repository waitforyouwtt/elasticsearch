package com.elastic.search.tools;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: luoxian
 * @Date: 2020/5/21 15:23
 * @Email: 15290810931@163.com
 */
public class SerializedLambdaUtils implements Serializable {

    private static Map<Class, SerializedLambda> CLASS_MAP = new ConcurrentHashMap<>();


    public static <T,U> String convertToFieldName(ESFunction<T, U> fn){
        SerializedLambda lambda = getSerializedLambda(fn);
        String str=lambda.getImplMethodName();
        String fieldName=str.substring(3,str.length());
        return StringUtils.uncapitalize(fieldName);
    }

    public static SerializedLambda getSerializedLambda(Serializable fn) {
        SerializedLambda lambda = CLASS_MAP.get(fn.getClass());
        if(lambda == null) {
            try {
                Method method = fn.getClass().getDeclaredMethod("writeReplace");
                method.setAccessible(Boolean.TRUE);
                lambda = (SerializedLambda) method.invoke(fn);
                CLASS_MAP.put(fn.getClass(), lambda);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lambda;
    }
}
