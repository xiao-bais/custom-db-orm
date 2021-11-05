
package com.custom.comm;

import com.alibaba.druid.sql.visitor.functions.Char;
import com.custom.annotations.DbKey;
import com.custom.annotations.DbTable;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.DbMediaType;
import com.custom.exceptions.CustomCheckException;
import com.custom.exceptions.ExceptionConst;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * @Author Xiao-Bai
 * @Date 2021/1/12 0012 22:07
 * @Version 1.0
 * @Description CommUtils
 */
public class CustomUtil {

    public static String getDataBase(String url){
        int lastIndex =  url.lastIndexOf("/");
        boolean is = url.indexOf("?") > 0;
        if(is){
            return url.substring(lastIndex+1, url.indexOf("?"));
        }else{
            return url.substring(url.lastIndexOf("/") + SymbolConst.DEFAULT_ONE);
        }
    }

    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-","").toLowerCase(Locale.CHINA);
    }

    public static boolean checkPrimaryKeyIsAutoIncrement(DbMediaType dbType){
        if("int".equals(dbType.getType())
            || "float".equals(dbType.getType())
            || "bigint".equals(dbType.getType())){
            return true;
        }
        return false;
    }

    public static boolean judgeDbType(Object el) throws Exception{
        return !(el instanceof String)
                && !(el instanceof Integer)
                && !(el instanceof Long)
                && !(el instanceof Double)
                && !(el instanceof Char)
                && !(el instanceof Short)
                && !(el instanceof Float)
                && !(el instanceof Boolean)
                && !(el instanceof Byte);
    }

    /**
     * 获取默认值
     */
    public static Object getDefaultVal(String type){
        if(type.equalsIgnoreCase("int")) return 0;
        if(type.equalsIgnoreCase("char")) return '\u0000';
        if(type.equalsIgnoreCase("double")) return 0.0d;
        if(type.equalsIgnoreCase("long") ) return 0L;
        if(type.equalsIgnoreCase("short")) return (short)0;
        if(type.equalsIgnoreCase("boolean")) return false;
        if(type.equalsIgnoreCase("float")) return 0.0f;
        if(type.equalsIgnoreCase("byte")) return (byte)0;
        return null;
    }

    /**
     * 根据java属性类型设置表字段类型
     */
    public static DbMediaType getDbFieldType(Class<?> type) {
        if (type.getName().toLowerCase().contains(("boolean"))) {
            return DbMediaType.DbBit;
        }
        if (type.getName().toLowerCase().contains(("double"))) {
            return DbMediaType.DbDouble;
        }
        if (type.getName().toLowerCase().contains(("int"))) {
            return DbMediaType.DbInt;
        }
        if (type.getName().toLowerCase().contains(("long"))) {
            return DbMediaType.DbBigint;
        }
        if (type.getName().toLowerCase().contains(("decimal"))) {
            return DbMediaType.DbDecimal;
        }
        if (type.getName().toLowerCase().contains(("date"))) {
            return DbMediaType.DbDate;
        }
        if (type.getName().toLowerCase().contains(("float"))) {
            return DbMediaType.DbFloat;
        }
        return DbMediaType.DbVarchar;
    }

    /**
     * example : a.name replace the a.`name`
     */
    public static String getJoinFieldStr(String field) {
        int index = field.indexOf(".");
        String fieldName = field.substring(index + SymbolConst.DEFAULT_ONE);
        String alias = field.substring(SymbolConst.DEFAULT_ZERO, index + SymbolConst.DEFAULT_ONE);
        return String.format("%s`%s`", alias, fieldName);
    }

    public static void main(String[] args) {
        String joinFieldStr = getJoinFieldStr("a.name");
        System.out.println("joinFieldStr = " + joinFieldStr);
    }


    /**
     * map转对象
     */
    public static <T> T mapToObject(Class<T> t, Map<String, Object> map) throws Exception  {
        if(map == null) return null;
        T obj = t.newInstance();
        BeanInfo beanInfo = Introspector.getBeanInfo(t);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            Method setter = property.getWriteMethod();
            if(setter != null){
                Object val = map.get(property.getName());
                if(val != null){
                    setter.invoke(obj, val);
                }
            }
        }
        return obj;
    }

    /**
     * 对象转map
     */
    public static <T> Map<String, Object> objectToMap(T t) throws Exception {
        if(t == null) return null;

        Map<String, Object> map = new HashMap<>();
        BeanInfo beanInfo = Introspector.getBeanInfo(t.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if(key.compareToIgnoreCase("class") == 0){
                continue;
            }
            Method getter = property.getReadMethod();
            Object val = getter != null ? getter.invoke(t) : null;
            map.put(key, val);
        }
        return map;
    }

    /**
     * 是否不为空
     */
    public static boolean isNotBlank(final  CharSequence cs) {
        return !isBlank(cs);
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
                sb.append(SymbolConst.UNDERLINE);
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
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == SymbolConst.UNDERLINE) {
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
     * 该类是否存在DbTable注解
     */
    public static <T> void isTableTag(Class<T> clazz) {
        if(!clazz.isAnnotationPresent(DbTable.class)) throw new CustomCheckException(ExceptionConst.EX_DBTABLE__NOTFOUND + clazz.getName());
    }

    /**
     * 该类是否有多个DbKey注解
     */
    public static <T> void isMoreDbKey(Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        int num = 0;
        for (Field field : fields) {
            if (field.isAnnotationPresent(DbKey.class)) {
                num++;
            }
        }
        if(num > 1) throw new CustomCheckException(ExceptionConst.EX_PRIMARY_REPEAT + clazz.getName());
    }

    /**
     * 该类是否存在主键
     */
    public static <T> boolean isKeyTag(Class<T> clazz){
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(DbKey.class)) return true;
        }
        return false;
    }







}
