package com.custom.jdbc;

import com.custom.annotations.DbField;
import com.custom.annotations.DbKey;
import com.custom.annotations.DbRelated;
import com.custom.dbconfig.GlobalConst;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.KeyStrategy;
import com.custom.exceptions.CustomCheckException;
import com.custom.utils.CommUtils;
import com.custom.utils.JudgeUtilsAx;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/7/4
 * @Description 解析字段处理类
 */
public class DbParserFieldHandler {

    private DbAnnotationsParser DbAnnoParser = null;

    public DbParserFieldHandler(){
        DbAnnoParser = new DbAnnotationsParser();
    }

    /**
     * 根据实体类获取对应数据库表中所有字段
     */
    <T> String[] getDbFields(Class<T> t){
        List<String> dbFields = new ArrayList<>();
        List<Map<String, Object>> elementList = DbAnnoParser.getParserByDbField(t);
        elementList.forEach(elementMap -> dbFields.add(String.valueOf(elementMap.get("dbFieldName"))));
        return dbFields.toArray(new String[0]);
    }

    /**
     * 获取一个类的所有标注@DbField注解的属性名称
     */
    <T> String[] getFiledNames(Class<T> t) throws Exception {
        Field[] fields = DbAnnoParser.getFields(t);
        List<String> fieldList = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].isAnnotationPresent(DbField.class)) {
                fieldList.add(fields[i].getName());
            }
        }
        return fieldList.toArray(new String[0]);
    }

    /**
     * 获取对象中所有的属性值
     */
    <T> List<Object> getFieldsVal(T t, String[] fields) throws Exception {
        List<Object> dataList = new ArrayList<>();
        for (String field : fields) {
            Object fieldValue = this.getFieldValue(t, field);
            dataList.add(fieldValue);
        }
        return dataList;
    }

    /**
     * 获取字段值
     */
    <T> Object getFieldValue(T t,  String fieldName) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Object value;
        String firstLetter;
        String getter;
        try {
            firstLetter = fieldName.substring(0, 1).toUpperCase();
            getter = SymbolConst.GET + firstLetter + fieldName.substring(1);
            Method method = t.getClass().getMethod(getter);
            value = method.invoke(t);
        }catch (NoSuchMethodException e){
            try {
                firstLetter = fieldName.substring(0, 1).toUpperCase();
                Method method = t.getClass().getMethod(SymbolConst.IS + firstLetter + fieldName.substring(1));
                value = method.invoke(t);
            }catch (NoSuchMethodException v) {
                Method method = t.getClass().getMethod(fieldName);
                value = method.invoke(t);
            }
        }
        return value;
    }

    /**
     * 获取实体类对应的表名
     */
    <T> String getDbTableName(Class<T> t) {
        return DbAnnoParser.getParserByDbTable(t).get("tableName").toString();
    }

    /**
     * 获取表别名
     */
    <T> String getDbTableAlias(Class<T> t) {
        return DbAnnoParser.getParserByDbTable(t).get("alias").toString();
    }

    /**
     * 该类是否有@DbRelation注解
     */
    private <T> boolean isDbRelationTag(Class<T> t) {
        Field[] fields = t.getDeclaredFields();
        for (Field field : fields) {
            if(field.isAnnotationPresent(DbRelated.class)) return true;
        }
        return false;
    }

    /**
     * 该类是否有@DbKey注解
     */
    <T> boolean isDbKeyTag(Class<T> t) {
        Field[] fields = t.getDeclaredFields();
        for (Field field : fields) {
            if(field.isAnnotationPresent(DbKey.class)) return true;
        }
        return false;
    }

    /**
     * 表连接的条件->是: 已经有一个, 否: 还没有
     */
    boolean isExistRelation(List<Map<String, String>> list, String tableName, String condition) {
        if(list.size() <= 0) return false;
        for (Map<String, String> tableMap : list) {
            if(tableName.equals(tableMap.get("joinTable")) && condition.equals(tableMap.get("condition"))) return true;
        }
        return false;
    }

    /**
     * 获取查询sql
     */
   <T> String getSelectSql(Class<T> t) throws Exception {
        return isDbRelationTag(t) ? getRelatedTableSql(t) : getBasicTableSql(t);
   }

   /**
    * 获取表的基础查询sql
    */
    private <T> String getBasicTableSql(Class<T> t) throws Exception {
        return String.format("select %s from %s %s ", getBasicFieldSql(t), getDbTableName(t), getDbTableAlias(t));
    }

    /**
     * 获取表查询的主表查询字段
     */
    private <T> String getBasicFieldSql(Class<T> t) throws Exception {
        StringJoiner fieldSql = new StringJoiner(SymbolConst.SEPARATOR_COMMA_1);
        String alias = getDbTableAlias(t);//获取表的别名

        if(isDbKeyTag(t)){
            Map<String, Object> dbKeyMap = DbAnnoParser.getParserByDbKey(t);
            String keySql = String.format("%s.`%s` `%s`", alias, dbKeyMap.get("dbKey"), dbKeyMap.get("fieldKey"));
            fieldSql.add(keySql);
        }

        List<Map<String, Object>> dbFieldMapList = DbAnnoParser.getParserByDbField(t);
        for (Map<String, Object> dbFieldMap : dbFieldMapList) {
            String dbFieldSql = String.format("%s.`%s` `%s`", alias, dbFieldMap.get("dbFieldName"), dbFieldMap.get("fieldName"));
            fieldSql.add(dbFieldSql);
        }
        return fieldSql.toString();
    }

    /**
    * 获取关联查询的sql
    */
    private <T> String getRelatedTableSql(Class<T> t) throws Exception {
        List<Map<String, String>> relationMapList = DbAnnoParser.getParserByDbRelated(t);
        StringBuilder relationSql = new StringBuilder();//最终生成的表连接sql
        List<Map<String, String>> joinTables = new ArrayList<>();//要关联的表
        StringJoiner relationTableFields = new StringJoiner(",");//查询关联表字段sql
        //需要查询的字段sql(basicTableSql + relationTableFields)
        StringBuilder queryFieldSql = new StringBuilder();

        for (Map<String, String> relationMap : relationMapList) {
            Map<String, String> tableMap = new HashMap<>();
            String joinSql = "";
            String joinTable = relationMap.get("joinTable");//关联表
            String condition = relationMap.get("condition");//关联条件
            String joinAlias = relationMap.get("joinAlias");//关联表别名
            String joinStyle = relationMap.get("joinStyle");//关联方式
            //如果有存在相同的关联条件,那么就不需要再次拼接关联
            if(!isExistRelation(joinTables, joinTable, condition)) {
                tableMap.put("joinTable", joinTable);
                tableMap.put("condition", condition);
                joinTables.add(tableMap);
                joinSql = String.format("\n%s %s %s on %s", joinStyle, joinTable, joinAlias ,condition);
                relationSql.append(joinSql);
            }
            relationTableFields.add(String.format("%s.`%s` `%s`",joinAlias, relationMap.get("dbField"), relationMap.get("fieldName")));
        }
        Map<String, Object> tableMap = DbAnnoParser.getParserByDbTable(t);
        queryFieldSql.append(String.format("%s %s", getBasicFieldSql(t), SymbolConst.SEPARATOR_COMMA_1));
        queryFieldSql.append(relationTableFields);
        return String.format("select %s \nfrom %s %s \n%s",
                queryFieldSql, tableMap.get("tableName"), tableMap.get("alias"), relationSql);
    }

    /**
     * 获取数据库主键
     */
    <T> String getDbFieldKey(Class<T> t) throws Exception {
        return String.valueOf(DbAnnoParser.getParserByDbKey(t).get("dbKey"));
    }

    /**
     * 获取主键对应的java属性字段
     */
    <T> String getFieldKey(Class<T> t) throws Exception {
        return String.valueOf(DbAnnoParser.getParserByDbKey(t).get("fieldKey"));
    }

    /**
     * 获取主键对应的java属性类型
     */
    <T> Field getFieldKeyType(Class<T> t) throws Exception {
        return t.getDeclaredField(String.valueOf(DbAnnoParser.getParserByDbKey(t).get("fieldKey")));
    }

    /**
     * 生成主键值
     */
    <T> Object generateKey(T t) throws Exception {
        Object value = null;
        Map<String, Object> parserByDbKey = DbAnnoParser.getParserByDbKey(t.getClass());
        KeyStrategy keyType = (KeyStrategy) parserByDbKey.get("strategy");
        String fieldKey = String.valueOf(parserByDbKey.get("fieldKey"));
        switch (keyType){
            case INPUT:
                value = getFieldValue(t, fieldKey);
                if(JudgeUtilsAx.isEmpty(value)) {
                    throw new CustomCheckException(GlobalConst.EX_PRIMARY_KEY_VALUE_NOT_EMPTY + t.getClass());
                }
                break;
            case AUTO:
                if(!isKeyByValid(t.getClass(), fieldKey)) {
                    throw new CustomCheckException(GlobalConst.EX_PRIMARY_CANNOT_MATCH + t.getClass());
                }
                value = 0;
                break;
            case UUID:
                Field field = t.getClass().getDeclaredField(fieldKey);
                Class<?> type = field.getType();
                if(type != String.class) {
                    throw new CustomCheckException(GlobalConst.EX_PRIMARY_CANNOT_MATCH + t.getClass());
                }
                value = CommUtils.getUUID();
                break;
        }
    return value;
    }

    /**
     * 判断主键是否是有效类型
     */
    <T> boolean isKeyByValid(Class<T> t, String fieldKey) throws Exception {
        Field field = t.getDeclaredField(fieldKey);
        Class<?> type = field.getType();
        return type == int.class || type == Integer.class || type == long.class || type == Long.class;
    }

    /**
     * 获取主键增值类型
     */
    <T> KeyStrategy getDbKeyType(Class<T> t) throws Exception {
        return (KeyStrategy) DbAnnoParser.getParserByDbKey(t).get("keyType");
    }

    /**
     * 通过表字段名称找到属性名称
     */
    <T> String getProFieldName(Class<T> t, String dbField) throws Exception {
        String fieldStr = null;
        if(isDbKeyTag(t)) {
            Map<String, Object> parserByDbKey = DbAnnoParser.getParserByDbKey(t);
            if(dbField.equals(parserByDbKey.get("dbKey"))) {
                fieldStr = String.valueOf(parserByDbKey.get("fieldKey"));
            }
        }
        List<Map<String, Object>> parserByDbField = DbAnnoParser.getParserByDbField(t);
        for (Map<String, Object> objectMap : parserByDbField) {
            if(dbField.equals(objectMap.get("dbFieldName"))) {
                fieldStr = String.valueOf(objectMap.get("fieldName"));
            }
        }
        List<Map<String, String>> relationList = DbAnnoParser.getParserByDbRelated(t);
        for (Map<String, String> objectMap : relationList) {
            if(dbField.equals(objectMap.get("dbField"))) {
                fieldStr = String.valueOf(objectMap.get("fieldName"));
            }
        }

        if(fieldStr == null) {
            throw new SQLException(String.format("Unknown column name: '%s'", dbField));
        }
        return fieldStr;
    }




}
