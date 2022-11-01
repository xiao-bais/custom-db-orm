package com.custom.action.condition;

import com.custom.action.condition.support.TableSupport;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.sqlparser.TableParseModel;

import java.util.Map;

/**
 * @author Xiao-Bai
 * @date 2022/11/1 12:58
 * @desc 对于条件构造器的表数据支持
 */
public class ConditionTableSupport<T> implements TableSupport<T> {

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



    public ConditionTableSupport(Class<T> entityClass) {
        this.parseModel = TableInfoCache.getTableModel(entityClass);
    }
}
