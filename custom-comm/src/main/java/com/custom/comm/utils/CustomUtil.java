
package com.custom.comm.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.custom.comm.annotations.DbTable;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.readwrite.ReadFieldHelper;
import com.custom.comm.readwrite.WriteFieldHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/1/12 0012 22:07
 * @Version 1.0
 * @Description CommUtils
 */
@Slf4j
@SuppressWarnings("unchecked")
public class CustomUtil extends StrUtils {


    /**
    * 是否是系统自定义的基础类型
    */
    public static boolean isBasicType(Object el) {
        return el instanceof CharSequence
                || el.getClass().isPrimitive()
                || el instanceof Integer
                || el instanceof Long
                || el instanceof Double
                || el instanceof Character
                || el instanceof Short
                || el instanceof Float
                || el instanceof Boolean
                || el instanceof Byte
                || el instanceof BigDecimal
                || el instanceof Date;
    }

    /**
     * 是否是系统自定义的基础类型
     */
    public static boolean isBasicClass(Class<?> cls) {
        return CharSequence.class.isAssignableFrom(cls)
                || cls.isPrimitive()
                || Integer.class.equals(cls)
                || Long.class.equals(cls)
                || Double.class.equals(cls)
                || Character.class.equals(cls)
                || Short.class.equals(cls)
                || Float.class.equals(cls)
                || Boolean.class.equals(cls)
                || Byte.class.equals(cls)
                || LocalDate.class.equals(cls)
                || BigInteger.class.equals(cls)
                || BigDecimal.class.equals(cls)
                || Date.class.equals(cls);
    }

    /**
     * 是否是java的原生对象
     */
    public static boolean isJavaOriginObject(Class<?> cls) {
        return cls.getPackage().getName().startsWith(Constants.JAVA_DOT);
    }


    /**
    * 是否是主键的允许类型
    */
    public static boolean isKeyAllowType(Class<?> type, Object val) {
        Asserts.illegal(!isBasicType(val),
                "不允许的主键类型：" + val.getClass());
        return CharSequence.class.isAssignableFrom(type)
                || Long.class.isAssignableFrom(type)
                || Integer.class.isAssignableFrom(type);
    }


    /**
    * 加载指定路径中文件的内容
    */
    public static String loadFiles(String filePath){
        String res = "";
        if(JudgeUtil.isEmpty(filePath)){
            log.error("The file cannot be found or the path does not exist");
            return res;
        }
        try {
            Resource resource = new ClassPathResource(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String str;
            while((str=br.readLine())!=null) {
                sb.append("\t").append(str);
            }
            res = sb.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 可执行的sql条件
     */
    public static String handleExecuteSql(String sql, Object[] params) {
        int symbolCount = countStr(sql, Constants.QUEST);
        int index = 0;
        while (index < symbolCount) {
            Object param = params[index];
            if(Objects.isNull(param)) {
                param = "null";
            }else if (param instanceof CharSequence) {
                param = String.format("'%s'", param);
            }
            sql = sql.replaceFirst("\\?", param.toString());
            index ++;
        }
        return sql;
    }


    public static boolean addParams(List<Object> thisParams, Object addVal) {
        Asserts.notNull(addVal);
        if (isBasicType(addVal)) {
            return thisParams.add(addVal);
        }
        if (addVal.getClass().isArray()) {
            return thisParams.addAll(Arrays.asList((Object[]) addVal));
        }
        if (addVal instanceof List) {
            return thisParams.addAll((List<Object>) addVal);
        }
        if (addVal instanceof Set) {
            return thisParams.addAll((Set<Object>) addVal);
        }
        throw new UnsupportedOperationException(String.format("Adding parameters of '%s' type is not supported", addVal.getClass()));
    }

    /**
     * 是否是不允许的泛型类型
     */
    public static boolean isNotAllowedGenericType(Class<?> genericType) {
       if (Object.class.equals(genericType) || Collection.class.isAssignableFrom(genericType)
            || Map.class.isAssignableFrom(genericType)) {
           return true;
       }
       return isBasicClass(genericType);
    }

    /**
     * 任意对象转json字符串
     */
    public static String objToJsonString(Object obj) {
        return JSON.toJSONString(obj);
    }

    /**
     * 反序列化实例化泛型对象(单泛型)
     */
    public static <T> T jsonParseToObject(String json, Class<T> type) {
        return JSON.parseObject(json, new TypeReference<T>(type){});
    }



}
