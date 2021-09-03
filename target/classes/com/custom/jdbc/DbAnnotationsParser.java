package com.custom.jdbc;

import com.custom.annotations.DbField;
import com.custom.annotations.DbKey;
import com.custom.annotations.DbRelated;
import com.custom.annotations.DbTable;
import com.custom.dbconfig.GlobalConst;
import com.custom.exceptions.DbAnnotationParserException;
import com.custom.utils.JudgeUtilsAx;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/7/4
 * @Description
 */
public class DbAnnotationsParser {

    /**
     * 获取@DbTable信息
     */
    public <T> Map<String, Object> getParserByDbTable(Class<T> t){
        if(t == null) {
            throw new NullPointerException();
        }
        if(!t.isAnnotationPresent(DbTable.class)){
            throw new DbAnnotationParserException(GlobalConst.EX_DBTABLE__NOTFOUND + t);
        }
        Map<String, Object> elementMap = new HashMap<>();
        DbTable name = t.getAnnotation(DbTable.class);
        String tableName = name.table();
        String className = t.getSimpleName();
        elementMap.put("tableName", JudgeUtilsAx.isNotEmpty(tableName) ? tableName : className);//表名称
        elementMap.put("className",className);//实体类名称
        elementMap.put("alias",name.alias());//表的别名
        return elementMap;
    }

    /**
     * 解析@DbKey注解
     */
    public <T>  Map<String,Object> getParserByDbKey(Class<T> t) throws Exception {
        if(t == null) {
            throw new NullPointerException();
        }
        JudgeUtilsAx.isMoreDbKey(t);
        Map<String, Object> elementMap = new HashMap<>();
        Field fieldKey = getFieldKey(t);
        DbKey annotation = fieldKey.getAnnotation(DbKey.class);
        if(annotation == null){
            throw new DbAnnotationParserException(GlobalConst.EX_DBKEY_NOTFOUND + t);
        }
        elementMap.put("dbKey", JudgeUtilsAx.isNotEmpty(annotation.value()) ? annotation.value() : fieldKey.getName());//主键字段名称
        elementMap.put("length", annotation.dbType().getLength());//主键长度
        elementMap.put("desc", annotation.desc());//主键说明
        elementMap.put("strategy", annotation.strategy());//主键策略
        elementMap.put("dbType", annotation.dbType());//主键数据类型
        elementMap.put("fieldKey", fieldKey.getName());//主键属性名称
        return elementMap;
    }

    /**
     * 获取主键字段的属性
     */
    public <T> Field getFieldKey(Class<T> t){
        Field[] fields = getFields(t);
        Field keyField = null;
        for (Field field : fields) {
            DbKey annotation = field.getAnnotation(DbKey.class);
            if(annotation != null){
                field.setAccessible(true);
                keyField = field;
                break;
            }
        }
        return keyField;
    }


    /**
     * 解析@DbField注解
     */
    public <T> List<Map<String, Object>> getParserByDbField(Class<T> t){
        if(t == null) throw new NullPointerException();
        List<Map<String, Object>> mapList = new ArrayList<>();
        Map<String, Object> elementMap;
        Field[] fields = getFields(t);

        for (Field field : fields) {
            if(!field.isAnnotationPresent(DbField.class)) continue;
            elementMap = new HashMap<>();
            DbField annotation = field.getAnnotation(DbField.class);
            elementMap.put("fieldType", annotation.fieldType());//数据库对应字段类型
            elementMap.put("fieldName", field.getName());//java实体类属性
            elementMap.put("dbFieldName", JudgeUtilsAx.isNotEmpty(annotation.value()) ? annotation.value() : field.getName());//数据库对应字段
            elementMap.put("length", annotation.fieldType().getLength());//数据库对应字段长度
            elementMap.put("desc", annotation.desc());//数据库对应字段说明
            elementMap.put("isNull", annotation.isNull());//是否允许存在空值
            mapList.add(elementMap);
        }
        return mapList;
    }

    /**
     * 获取所有字段
     */
    public <T> Field[] getFields(Class<T> t){
        Class<?> clz = t;
        List<Field> fieldList = new ArrayList<>();
        while (clz != null && !clz.getName().toLowerCase().equals("java.lang.object")){
            fieldList.addAll(Arrays.asList(clz.getDeclaredFields()));
            clz = clz.getSuperclass();
        }
        if(fieldList.size() == 0) throw new DbAnnotationParserException(GlobalConst.EX_DBFIELD__NOTFOUND + t);
        return fieldList.toArray(new Field[0]);
    }
    
    /**
     * 解析@DbRelated注解
     */
    public <T> List<Map<String,String>> getParserByDbRelated(Class<T> t) {
        if(t == null) {
            throw new NullPointerException();
        }
        List<Map<String,String>> mapList = new ArrayList<>();
        Map<String,String> relationMap = null;
        Field[] fields = getFields(t);
        for (Field field : fields) {
            if(!field.isAnnotationPresent(DbRelated.class)) continue;
            relationMap = new HashMap<>();
            DbRelated annotation = field.getAnnotation(DbRelated.class);
            String dbField = JudgeUtilsAx.isNotEmpty(annotation.field()) ? annotation.field() : field.getName();
            relationMap.put("joinTable", annotation.joinTable());//关联表
            relationMap.put("joinAlias", annotation.joinAlias());//关联表别名
            relationMap.put("condition", annotation.condition());//关联条件
            relationMap.put("joinStyle", annotation.joinStyle());//关联方式
            relationMap.put("dbField", dbField);//表字段名称
            relationMap.put("fieldName", field.getName());//实体类属性名称
            mapList.add(relationMap);
        }
        return mapList;
    }
    



}
