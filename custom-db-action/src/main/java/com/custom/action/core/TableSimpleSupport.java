package com.custom.action.core;

import com.custom.action.condition.support.TableSupport;
import com.custom.action.core.TableInfoCache;
import com.custom.action.core.TableParseModel;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * 对于条件构造器的相关表数据支持
 * @author   Xiao-Bai
 * @since  2022/11/1 12:58
 */
public class TableSimpleSupport<T> implements TableSupport {

    private final TableParseModel<T> parseModel;

    @Override
    public String table() {
        return parseModel.getTable();
    }

    @Override
    public String alias() {
        return parseModel.getAlias();
    }

    @Override
    public Map<String, String> fieldMap() {
        return parseModel.getFieldMapper();
    }

    @Override
    public Map<String, String> columnMap() {
        return parseModel.getColumnMapper();
    }

    @Override
    public List<Field> fields() {
        return parseModel.getFields();
    }


    public TableSimpleSupport(Class<T> entityClass) {
        this.parseModel = TableInfoCache.getTableModel(entityClass);
    }
}
