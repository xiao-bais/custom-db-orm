package com.custom.action.condition;

import com.custom.action.condition.support.TableSupport;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.sqlparser.TableParseModel;

import java.util.Map;

/**
 * @author Xiao-Bai
 * @date 2022/11/1 12:58
 * @desc 对于条件构造器的相关表数据支持
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


    public TableSimpleSupport(Class<T> entityClass) {
        this.parseModel = TableInfoCache.getTableModel(entityClass);
    }
}
