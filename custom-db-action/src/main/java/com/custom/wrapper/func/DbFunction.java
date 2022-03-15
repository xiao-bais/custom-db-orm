package com.custom.wrapper.func;

import com.custom.sqlparser.TableInfoCache;
import com.custom.wrapper.ColumnParseHandler;
import com.custom.wrapper.SFunction;

import java.lang.reflect.Field;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/15 19:55
 * @Desc：
 **/
public class DbFunction<T> {


    private final ColumnParseHandler<T> columnParseHandler;
    private final Class<T> entityClass;

    public DbFunction(Class<T> entityClass) {
        this.entityClass = entityClass;
        Field[] fields = TableInfoCache.getTableModel(entityClass).getFields();
        columnParseHandler = new ColumnParseHandler<>(entityClass, fields);
    }

    public String sum(SFunction<T, ?> column) {

        return null;
    }

}
