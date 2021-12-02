package com.custom.sqlparser;

import com.custom.annotations.*;
import com.custom.comm.CustomUtil;
import com.custom.dbconfig.SymbolConst;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/2 16:34
 * @Desc：对于@DbRelated注解的解析
 **/
public class DbRelationParserModel<T> extends AbstractTableModel<T> {

    private Class<T> cls;

    private Field field;

    private String joinTable;

    private String joinAlias;

    private String condition;

    private String joinStyle;

    private String fieldName;

    private String column;



    public DbRelationParserModel(Class<T> cls, String table, String alias) {
        this.cls = cls;
        super.setTable(table);
        super.setAlias(alias);
    }

    public DbRelationParserModel(Class<T> cls, Field field, String table, String alias) {
        this.cls = cls;
        this.field = field;
        super.setTable(table);
        super.setAlias(alias);
    }

    /**
    * 关联的sql分为两部分
     * 一是 @DbJoinTables注解，二是@DbRelated注解
     * 默认按顺序载入，优先加载DbJoinTables注解
    */
    @Override
    public String buildTableSql() {

        StringBuilder tableSql = new StringBuilder("select");
        List<DbRelationParserModel<T>> relatedParserModels = new ArrayList<>();
        DbKeyParserModel<T> dbKeyParserModel = null;
        List<DbFieldParserModel<T>> fieldParserModels = new ArrayList<>();
        List<String> joinTableParserModels = new ArrayList<>();
        Map<String, String> joinTableParserModelMap = new HashMap<>();

        DbJoinTables joinTables = this.cls.getAnnotation(DbJoinTables.class);
        if(joinTables != null) {
            Arrays.stream(joinTables.value()).map(DbJoinTable::value).forEach(joinTableParserModels::add);
        }

        Field[] fields = CustomUtil.getFields(this.cls);
        for (Field field : fields) {
            if(field.isAnnotationPresent(DbRelated.class)) {
                DbRelationParserModel<T> relatedParserModel = new DbRelationParserModel<>(this.cls, field, super.getTable(), super.getAlias());
                relatedParserModels.add(relatedParserModel);
            }
            else if(field.isAnnotationPresent(DbKey.class)) {
                dbKeyParserModel = new DbKeyParserModel<>(field, super.getTable(), super.getAlias());
            }
            else if(field.isAnnotationPresent(DbField.class)) {
                DbFieldParserModel<T> fieldParserModel = new DbFieldParserModel<>(field);
                fieldParserModels.add(fieldParserModel);
            }
            else if(field.isAnnotationPresent(DbMap.class)) {
                DbMap annotation = field.getAnnotation(DbMap.class);
                joinTableParserModelMap.put(CustomUtil.getJoinFieldStr(annotation.value().trim()), field.getName());
            }
        }


        StringJoiner baseFieldSql = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
        if(dbKeyParserModel != null) {

        }




        return null;
    }

    @Override
    public Object getValue(T x, String fieldName) {
        return null;
    }

    @Override
    public String getFieldSql() {
        return String.format("%s.`%s`", super.getAlias(), this.column);
    }

    @Override
    public String getSelectFieldSql() {
        return String.format("%s.`%s` `%s`", super.getAlias(), this.column, this.fieldName);
    }
}
