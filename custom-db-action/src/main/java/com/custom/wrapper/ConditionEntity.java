package com.custom.wrapper;

import com.custom.enums.DbSymbol;
import com.custom.enums.ExecuteMethod;
import com.custom.sqlparser.TableSqlBuilder;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/11 13:06
 * @Desc：条件构造器
 **/
@SuppressWarnings("unchecked")
public class ConditionEntity<T> extends AbstractWrapper<T, ConditionEntity<T>>{

    private TableSqlBuilder<T> tableSqlBuilder;

    private Class<T> cls;

    public ConditionEntity(Class<T> entityClass) {
        this.cls = entityClass;
        this.tableSqlBuilder = new TableSqlBuilder<>(cls, ExecuteMethod.NONE);
    }



    public ConditionEntity<T> where(DbSymbol dbSymbol, boolean condition, String column, Object val1, Object val2, String express) {
        adapterCondition(dbSymbol, condition, column, val1, val2, express);
        return this;
    }


    @Override
    public String toString() {
//        return this.getConditional().toString();
        return null;
    }


    @Override
    public ConditionEntity<T> adapter(DbSymbol dbSymbol, String column, Object val) {
        adapterCondition(dbSymbol, true, column, val, null, null);
        return this;
    }

    @Override
    public ConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, String column, Object val) {
        adapterCondition(dbSymbol, condition, column, val, null, null);
        return this;
    }

    @Override
    public ConditionEntity<T> adapter(DbSymbol dbSymbol, boolean condition, String column, Object val1, Object val2) {
        adapterCondition(dbSymbol, condition, column, val1, val2, null);
        return this;
    }


}
