
package com.custom.comm;

import com.alibaba.druid.sql.visitor.functions.Char;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.DbMediaType;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
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
public class CommUtils {

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








}
