package com.custom.jdbc;

import com.custom.dbconfig.ExceptionConst;
import com.custom.enums.DbMediaType;
import com.custom.enums.KeyStrategy;
import com.custom.exceptions.CustomCheckException;
import com.custom.comm.CommUtils;
import com.custom.comm.JudgeUtilsAx;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @Author Xiao-Bai
 * @Date 2021/8/19
 * @Description 拼接sql语句专用类
 */
public class TableSpliceSql {

    private DbAnnotationsParser annotationsParser;

    public TableSpliceSql(DbAnnotationsParser annotationsParser) {
        this.annotationsParser = annotationsParser;
    }

    /**
     * 创建表
     */
    public <T> String getCreateTableSql(Class<T> t) throws Exception {
        List<Map<String, Object>> fieldMapList = annotationsParser.getParserByDbField(t);
        String tableName = annotationsParser.getParserByDbTable(t).get("tableName").toString();
        return String.format("create table `%s` (\n%s %s) ",
                tableName, this.createTaleByFieldKeySql(t), this.createTableByFieldSql(fieldMapList));
    }

    /**
     * 获取除主键外的其他字段创建语句
     */
    private String createTableByFieldSql(List<Map<String, Object>> mapList) {
        StringJoiner createFieldSql = new StringJoiner(",");
        for (Map<String, Object> map : mapList) {
            String fieldName = map.get("dbFieldName").toString();
            //如果注解上没注明对应的表字段,就以java属性字段来填充
            if(JudgeUtilsAx.isEmpty(fieldName)){
                fieldName = map.get("fieldName").toString();
            }
            DbMediaType fieldType = (DbMediaType) map.get("fieldType");

            String length = "";
            if(DbMediaType.DbDate != fieldType && DbMediaType.DbDateTime != fieldType) {
                length = String.format("(%s)",map.get("length").toString());
            }
            String isNullField = "";
            boolean isNull = (Boolean) map.get("isNull");
            if(!isNull) {
                isNullField ="not null";
            }
            String createField = String.format("`%s` %s%s %s comment '%s'\n",
                    fieldName, fieldType.getType(), length, isNullField, map.get("desc"));
            createFieldSql.add(createField);
        }
        return createFieldSql.toString();
    }

    /**
     * 获取主键字段的创建语句
     */
    private <T> String createTaleByFieldKeySql(Class<T> t) throws Exception {
        String primaryKeySql = "";
        Map<String, Object> map = annotationsParser.getParserByDbKey(t);
        if(map.isEmpty()) return primaryKeySql;

        String dbKey = map.get("dbKey").toString();
        if(JudgeUtilsAx.isEmpty(dbKey)) {
            dbKey = map.get("fieldKey").toString();
        }
        DbMediaType dbType = (DbMediaType) map.get("dbType");//获取主键数据类型
        KeyStrategy keyType = (KeyStrategy) map.get("strategy");//获取主键增值类型
        String keyStrategy = "";
        if(KeyStrategy.AUTO.equals(keyType)) {
            if(!CommUtils.checkPrimaryKeyIsAutoIncrement(dbType))
                throw new CustomCheckException(ExceptionConst.EX_PRIMARY_CANNOT_MATCH);
                keyStrategy = "auto_increment";
        }
        primaryKeySql = String.format("`%s` %s(%s) primary key not null %s comment '%s' \n,",
                dbKey, dbType.getType(), map.get("length"), keyStrategy, map.get("desc"));
        return primaryKeySql;
    }
}
