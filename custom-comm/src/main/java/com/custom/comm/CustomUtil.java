
package com.custom.comm;

import com.custom.comm.annotations.DbTable;
import com.custom.comm.exceptions.ExThrowsUtil;
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
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/1/12 0012 22:07
 * @Version 1.0
 * @Description CommUtils
 */
@Slf4j
@SuppressWarnings("unchecked")
public class CustomUtil {

    public static String getDataBase(String url){
        int lastIndex =  url.lastIndexOf("/");
        boolean is = url.indexOf("?") > 0;
        if(is){
            return url.substring(lastIndex+1, url.indexOf("?"));
        }else{
            return url.substring(url.lastIndexOf("/") + SymbolConstant.DEFAULT_ONE);
        }
    }

    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-","").toLowerCase(Locale.CHINA);
    }


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
                || BigDecimal.class.equals(cls)
                || Date.class.equals(cls);
    }

    /**
     * 是否是java的原生对象
     */
    public static boolean isJavaOriginObject(Class<?> cls) {
        return cls.getPackage().getName().startsWith(SymbolConstant.JAVA_DOT);
    }


    /**
    * 是否是主键的允许类型
    */
    public static boolean isKeyAllowType(Class<?> type, Object val) {
        if(!isBasicType(val.getClass())) {
            ExThrowsUtil.toCustom("不允许的主键类型：" + val.getClass());
        }
        return CharSequence.class.isAssignableFrom(type)
                || Long.class.isAssignableFrom(type)
                || Integer.class.isAssignableFrom(type);
    }

    /**
     * 读取对象的属性值
     */
    public static <T> boolean writeFieldValue(Object writeValue, T waitWriteEntity, String fieldName, Class<?> writeType) throws NoSuchFieldException {
        WriteFieldHelper<T> readFieldHelper = new WriteFieldHelper<>(writeValue, waitWriteEntity, fieldName, writeType);
        return readFieldHelper.writeValue();
    }

    /**
     * 读取对象的属性值
     */
    public static <T> Object readFieldValue(T entity, String fieldName) throws NoSuchFieldException {
        ReadFieldHelper<T, Object> readFieldHelper = new ReadFieldHelper<>(entity, fieldName);
        return readFieldHelper.readObjectValue();
    }


    /**
     * 是否不为空
     */
    public static boolean isNotBlank(final  CharSequence cs) {
        return !isBlank(cs);
    }


    /**
    * 类名转首字母小写
    */
    public static String toIndexLower(String text) {
        String res = SymbolConstant.EMPTY;
        if(JudgeUtil.isEmpty(text)) {
            return res;
        }
        String first = text.substring(0, 1).toLowerCase();
        res = first + text.substring(1);
        return res;
    }

    public static void main(String[] args) {
        boolean javaOriginObject = isJavaOriginObject(JudgeUtil.class);
        System.out.println("javaOriginObject = " + javaOriginObject);


    }

    /**
     * 是否为空
     */
    public static boolean isBlank(final CharSequence cs) {
        if (cs == null) {
            return true;
        }
        int l = cs.length();
        if (l > 0) {
            for (int i = 0; i < l; i++) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 驼峰转下划线
     */
    public static String camelToUnderline(String param) {
        if (isBlank(param)) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append(SymbolConstant.UNDERLINE);
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    /**
     * 字符串下划线转驼峰
     */
    public static String underlineToCamel(String param) {
        if (isBlank(param)) {
            return SymbolConstant.EMPTY;
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == SymbolConstant.UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    /**
    * 获取一个类的所有属性（包括父类）
    */
    public static <T> Field[] loadFields(Class<T> t, boolean checkDbField) {
        Class<?> clz = t;
        DbTable thisDbTable = t.getAnnotation(DbTable.class);
        List<Field> fieldList = new ArrayList<>();
        while (!clz.equals(Object.class)) {
            Arrays.stream(clz.getDeclaredFields()).forEach(field -> {
                int modifiers = field.getModifiers();
                if (Modifier.isPrivate(modifiers)
                        && !Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers)) {
                    fieldList.add(field);
                }
            });
            DbTable parentDbTable = clz.getSuperclass().getAnnotation(DbTable.class);
            if (Objects.isNull(parentDbTable)
                    || thisDbTable.equals(parentDbTable)) {
                clz = clz.getSuperclass();
                if (clz.equals(Object.class)) {
                    break;
                }
            }
        }
        if (fieldList.size() == 0 && checkDbField) {
            ExThrowsUtil.toCustom("@DbField not found in class " + t);
        }
        return fieldList.toArray(new Field[0]);
    }

    public static <T> Field[] loadFields(Class<T> t){
        return loadFields(t, true);
    }


    /**
    * 查找字符串出现次数
    */
    public static int countStr(String str,String rex) {
        int num = 0;
        while (str.contains(rex)) {
            str = str.substring(str.indexOf(rex) + rex.length());
            num ++;
        }
        return num;
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
                sb.append(str);
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
        int symbolCount = countStr(sql, SymbolConstant.QUEST);
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

    public static Map<String, Object> beanToMap(Object bean) throws IntrospectionException {
        Class<?> thisClass = bean.getClass();
        Map<String, Object> resMap = new HashMap<>();
        BeanInfo beanInfo = Introspector.getBeanInfo(thisClass);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String propertyName = property.getName();
            if (propertyName.equals("class")) {
                continue;
            }
            Method readMethod = property.getReadMethod();
            try {
                Object proValue = readMethod.invoke(bean);
                resMap.put(propertyName, proValue);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return resMap;
    }


    public static boolean addParams(List<Object> thisParams, Object addVal) {
        Asserts.notNull(addVal);
        if (isBasicType(addVal)) {
            return thisParams.add(addVal);
        }
        if (addVal instanceof List) {
            return thisParams.addAll((List<Object>) addVal);
        }
        if (addVal instanceof Set) {
            return thisParams.addAll((Set<Object>) addVal);
        }
        ExThrowsUtil.toUnSupport(String.format("Adding parameters of '%s' type is not supported", addVal.getClass()));
        return false;
    }

    /**
     * 是否是不允许的泛型类型
     */
    public static boolean isNotAllowedGenericType(Class<?> genericType) {
       if (Object.class.equals(genericType)) {
           return true;
       }
       if (Collection.class.isAssignableFrom(genericType)) {
           return true;
       }
       if (Map.class.isAssignableFrom(genericType)) {
           return true;
       }
       return isBasicClass(genericType);
    }

    /**
     * 字符串倒转
     */
    public static String reverse(String str) {
        Asserts.notEmpty(str);
        char[] chars = str.toCharArray();
        int n = chars.length - 1;
        for (int i = 0; i < chars.length / 2; i++) {
            char temp = chars[i];
            chars[i] = chars[n - i];
            chars[n - i] = temp;
        }
        return new String(chars);
    }



}
