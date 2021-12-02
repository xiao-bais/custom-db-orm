package com.custom.sqlparser;

import com.custom.annotations.DbField;
import com.custom.annotations.DbKey;
import com.custom.annotations.DbTable;
import com.custom.comm.CustomUtil;
import com.custom.dbconfig.SymbolConst;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/2 14:10
 * @Desc：
 **/
public class BaseTableSqlBuilder<T> {

    private Class<T> cls;

    private T t;

    private String table;

    private DbKeyParserModel<T> dbKeyParserModel = null;

    private List<DbFieldParserModel<T>> fieldBaseModels = new ArrayList<>();

    private List<DbRelationParserModel<T>> fieldRelatedModels;

    private String alias;

    private String className;

    public String createTableSql() {

        StringBuilder createTableSql = new StringBuilder();

        StringJoiner fieldSql = new StringJoiner(SymbolConst.SEPARATOR_COMMA_1);
        if(this.dbKeyParserModel != null) {
            fieldSql.add(dbKeyParserModel.buildTableSql() + "\n");
        }

        if(!this.fieldBaseModels.isEmpty()) {
            fieldBaseModels.stream().map(dbFieldParserModel -> dbFieldParserModel.buildTableSql() + "\n").forEach(fieldSql::add);
        }

        createTableSql.append(String.format("create table `%s` (\n%s)", this.table, fieldSql.toString()));
        return createTableSql.toString();
    }

    private String select() {



        return null;
    }

    private String update() {

        return null;
    }

    private String delete() {

        return null;
    }

    private String insert() {

        return null;
    }




    public BaseTableSqlBuilder(Class<T> cls) {
        this.cls = cls;
        DbTable annotation = cls.getAnnotation(DbTable.class);
        this.alias = annotation.alias();
        this.table = annotation.table();
        this.className = cls.getSimpleName();
        Field[] fields = CustomUtil.getFields(cls);
        for (Field field : fields) {
            if(field.isAnnotationPresent(DbKey.class)) {
                this.dbKeyParserModel = new DbKeyParserModel<>(field);
            }else if(field.isAnnotationPresent(DbField.class)) {
                this.fieldBaseModels.add(new DbFieldParserModel<>(field));
            }
        }
    }

    public BaseTableSqlBuilder(T t) {
        this.t = t;
    }


}
