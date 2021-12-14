package com.custom.wrapper;

import com.custom.dbaction.AbstractSqlBuilder;
import com.custom.enums.DbSymbol;
import com.custom.enums.ExecuteMethod;
import com.custom.sqlparser.TableSqlBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2021/12/11 13:06
 * @Desc：条件构造器
 **/
@SuppressWarnings("unchecked")
public class ConditionEntity<T> {

    private TableSqlBuilder<T> tableSqlBuilder;

    private Class<T> cls;

    public ConditionEntity()  {


    }

    public void init(){
        Type type1 = getClass().getGenericSuperclass();
        Type type = this.getClass().getGenericSuperclass();
        if(type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            cls = (Class<T>) typeArguments[0];
            System.out.println("cls-->" + cls.getName());
            this.tableSqlBuilder = new TableSqlBuilder<>(cls, ExecuteMethod.NONE);
        }
    }



    public ConditionEntity<T> where(DbSymbol dbSymbol, boolean condition, String column, Object val1, Object val2, String express) {
//        adapterCondition(dbSymbol, condition, column, val1, val2, express);
        return this;
    }


    @Override
    public String toString() {
//        return this.getConditional().toString();
        return null;
    }


//    @Override
//    public void adapter(DbSymbol dbSymbol, String column, Object val) {
//        adapterCondition(dbSymbol, true, column, val, null, null);
//    }
//
//    @Override
//    public void adapter(DbSymbol dbSymbol, boolean condition, String column, Object val) {
//        adapterCondition(dbSymbol, condition, column, val, null, null);
//    }
//
//    @Override
//    public void adapter(DbSymbol dbSymbol, boolean condition, String column, Object val1, Object val2) {
//        adapterCondition(dbSymbol, condition, column, val1, val2, null);
//    }
//
//    @Override
//    public void adapter(DbSymbol dbSymbol, boolean condition, String column, Object val, String express) {
//        adapterCondition(dbSymbol, condition, column, val, null, express);
//    }
}
