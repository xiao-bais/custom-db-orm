
package com.custom.comm;

import com.alibaba.druid.sql.visitor.functions.Char;
import com.custom.annotations.DbKey;
import com.custom.annotations.DbRelated;
import com.custom.annotations.DbTable;
import com.custom.dbconfig.DbDataSource;
import com.custom.dbconfig.DbFieldsConst;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.DbMediaType;
import com.custom.exceptions.CustomCheckException;
import com.custom.exceptions.DbAnnotationParserException;
import com.custom.exceptions.ExceptionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    public static String getConnKey(DbDataSource dbDataSource) {
        return String.format("%s-%s-%s-%s", dbDataSource.getUrl(), dbDataSource.getUsername(), dbDataSource.getPassword(), dbDataSource.getDatabase());
    }

    public static boolean isDataSourceEmpty(DbDataSource dbDataSource) {
        return JudgeUtilsAx.isEmpty(dbDataSource.getUrl()) || JudgeUtilsAx.isEmpty(dbDataSource.getUsername()) || JudgeUtilsAx.isEmpty(dbDataSource.getPassword());
    }

    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-","").toLowerCase(Locale.CHINA);
    }

    public static boolean checkPrimaryKeyIsAutoIncrement(DbMediaType dbType){
        return "int".equals(dbType.getType())
                || "float".equals(dbType.getType())
                || "bigint".equals(dbType.getType());
    }

    /**
     * 该类是否有@DbRelation注解
     */
    public static <T> boolean isDbRelationTag(Class<T> t) {
        Field[] fields = t.getDeclaredFields();
        for (Field field : fields) {
            if(field.isAnnotationPresent(DbRelated.class)) return true;
        }
        return false;
    }

    /**
    * 是否是系统自定义的基础类型
    */
    public static boolean isBasicType(Object el) {
        return (el.equals(String.class))
                || (el.equals(Integer.class) || el.equals(int.class))
                || (el.equals(Long.class) || el.equals(long.class))
                || (el.equals(Double.class) || el.equals(double.class))
                || (el.equals(Char.class) || el.equals(char.class))
                || (el.equals(Short.class) || el.equals(short.class))
                || (el.equals(Float.class) || el.equals(float.class))
                || (el.equals(Boolean.class) || el.equals(boolean.class))
                || (el.equals(Byte.class) || el.equals(byte.class))
                || (el.equals(BigDecimal.class));
    }

    /**
    * 是否是主键的允许类型
    */
    public static boolean isKeyAllowType(Class<?> type, Object val) {
        if(!isBasicType(val.getClass())) {
            throw new CustomCheckException("不允许的主键类型：" + val+ "(" + val.getClass() + ")");
        }
        if(type == String.class) {
            return true;
        }
        if(type == Long.class) {
            return (long) val > 0;
        }
        if(type == Integer.class) {
            return (int) val > 0;
        }
        return false;
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




    /**
     * map转对象
     */
    public static <T> T mapToObject(Class<T> t, Map<String, Object> map) throws IllegalAccessException, IntrospectionException, InvocationTargetException, InstantiationException {
        if(map == null) return null;
        T obj = null;
        obj = t.newInstance();
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
    * 类名转首字母小写
    */
    public static String toIndexLower(String text) {
        String res = SymbolConst.EMPTY;
        if(JudgeUtilsAx.isEmpty(text)) {
            return res;
        }
        String first = text.substring(0, 1).toLowerCase();
        res = first + text.substring(1);
        return res;
    }

    public static void main(String[] args) {
        String userInfo = toIndexLower("UserInfo");
        System.out.println("userInfo = " + userInfo);


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
        Field[] fields = getFields(clazz);
        for (Field field : fields) {
            if (field.isAnnotationPresent(DbKey.class)) return true;
        }
        return false;
    }

    /**
    * 获取一个类的所有属性（包括父类）
    */
    public static <T> Field[] getFields(Class<T> t){
        Class<?> clz = t;
        List<Field> fieldList = new ArrayList<>();
        while (clz != null && !clz.getName().toLowerCase().equals("java.lang.object")){
            fieldList.addAll(Arrays.asList(clz.getDeclaredFields()));
            clz = clz.getSuperclass();
        }
        if(fieldList.size() == 0) throw new DbAnnotationParserException(ExceptionConst.EX_DBFIELD__NOTFOUND + t);
        return fieldList.toArray(new Field[0]);
    }

    /**
    * sql#{name} 替换为 #{name} 返回起始的下标位置
    */
    public static int[] replaceSqlRex(String sql, String beginRex, String endRex, int index) {

        int[] indexes = new int[3];
        int start = sql.indexOf(beginRex, index);
        int end = sql.indexOf(endRex, start);
        if(start == -1 || end == -1)
            return null;
        else if(start > 0 && end > 0){
            indexes[0] = start;
            indexes[1] = end;
            indexes[2] = end + 1;
            return indexes;
        }
        return null;
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
    * 手动处理预编译的sql
    */
    public static String prepareSql(String sql, Object... params) {
        StringBuilder sqlBuilder = new StringBuilder(sql);
        if(params.length > 0) {
            int index = 0;
            int symbolNums = countStr(sql, SymbolConst.QUEST);
            for (int i = 0; i < symbolNums; i++) {
                index = sqlBuilder.indexOf(SymbolConst.QUEST, index);
                String rexStr = params[i] instanceof String ? String.format("'%s'", params[i]) : params[i].toString();
                sqlBuilder.replace(index, index + 1, rexStr);
                index += rexStr.length() - 1;
            }
        }
        return sqlBuilder.toString();
    }


    /**
    * 加载指定路径中文件的内容
    */
    public static String loadFiles(String filePath){
        String res = "";
        if(JudgeUtilsAx.isEmpty(filePath)){
            log.error("找不到文件或不存在该路径");
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
     * 消除sql条件中的第一个and/or
     */
    public static String trimSqlCondition(String condition, String symbol) {
        String finalCondition = condition;
        if(condition.trim().startsWith(symbol)) {
            finalCondition = condition.replaceFirst(symbol, SymbolConst.EMPTY);
        }
        return finalCondition.trim();
    }

    public static String trimSqlCondition(String condition) {
        String finalCondition = condition;
        if(condition.trim().startsWith(SymbolConst.AND)) {
            finalCondition = condition.replaceFirst(SymbolConst.AND, SymbolConst.EMPTY);
        }
        return finalCondition.trim();
    }

    /**
     * 可执行的sql打印
     */
    public static String handleExecuteSql(String sql, Object[] params) {
        int symbolSize = countStr(sql, SymbolConst.QUEST);
        int index = 0;
        while (index < symbolSize) {
            Object param = params[index];
            if(param.getClass() == String.class) {
                param = String.format("'%s'", param);
            }
            sql = sql.replaceFirst("\\?", param.toString());
            index ++;
        }
        return sql;
    }




}
