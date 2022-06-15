
package com.custom.comm;

import com.custom.comm.annotations.DbTable;
import com.custom.comm.exceptions.ExThrowsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

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
    * 是否是主键的允许类型
    */
    public static boolean isKeyAllowType(Class<?> type, Object val) {
        if(!isBasicType(val.getClass())) {
            ExThrowsUtil.toCustom("不允许的主键类型：" + val.getClass());
        }
        if(CharSequence.class.isAssignableFrom(type)) {
            return true;
        }
        if(Long.class.isAssignableFrom(type)) {
            return (long) val > 0;
        }
        if(Integer.class.isAssignableFrom(type)) {
            return (int) val > 0;
        }
        return false;
    }

    /**
     * 获取字java属性值
     */
    public static <T> Object getFieldValue(T entity, String fieldName) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        JudgeUtil.checkObjNotNull(entity, fieldName);
        Object value;
        String firstLetter;
        String getter;
        try {
            if(RexUtil.hasRegex(fieldName, RexUtil.back_quotes)) {
                fieldName = RexUtil.regexStr(fieldName, RexUtil.back_quotes);
            }
            if (Objects.isNull(fieldName)) return null;
            firstLetter = fieldName.substring(0, 1).toUpperCase();
            getter = SymbolConstant.GETTER + firstLetter + fieldName.substring(1);
            Method method = entity.getClass().getMethod(getter);
            value = method.invoke(entity);
        }catch (NoSuchMethodException e){
            try {
                firstLetter = fieldName.substring(0, 1).toUpperCase();
                Method method = entity.getClass().getMethod(SymbolConstant.IS + firstLetter + fieldName.substring(1));
                value = method.invoke(entity);
            }catch (NoSuchMethodException v) {
                Method method = entity.getClass().getMethod(fieldName);
                value = method.invoke(entity);
            }
        }
        return value;
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
    public static <T> Field[] getFields(Class<T> t, boolean checkDbField){
        Class<?> clz = t;
        DbTable thisDbTable = t.getAnnotation(DbTable.class);
        List<Field> fieldList = new ArrayList<>();
        while (clz != null && !clz.getName().equalsIgnoreCase("java.lang.object")){
            fieldList.addAll(Arrays.asList(clz.getDeclaredFields()));
            DbTable parentDbTable = clz.getAnnotation(DbTable.class);
            if (parentDbTable != null && parentDbTable.table().equals(thisDbTable.table())) {
                clz = clz.getSuperclass();
            }
        }
        if(fieldList.size() == 0 && checkDbField) ExThrowsUtil.toCustom("@DbField not found in class "+ t);
        return fieldList.toArray(new Field[0]);
    }

    public static <T> Field[] getFields(Class<T> t){
        return getFields(t, true);
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




}
