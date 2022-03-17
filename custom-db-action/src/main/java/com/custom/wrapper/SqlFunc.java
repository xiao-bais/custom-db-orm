package com.custom.wrapper;

import com.custom.dbconfig.SymbolConst;
import com.custom.enums.SqlAggregate;
import com.custom.sqlparser.TableInfoCache;

import java.util.Map;
import java.util.StringJoiner;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/15 19:55
 * @Desc：sql查询函数
 **/
public class SqlFunc<T> {

    private final ColumnParseHandler<T> columnParseHandler;
    private final Map<String, String> fieldMapper;
    private final StringJoiner selectFuncs;

    public SqlFunc(Class<T> entityClass) {
        columnParseHandler = new ColumnParseHandler<>(entityClass);
        fieldMapper = TableInfoCache.getFieldMap(entityClass);
        selectFuncs = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
    }

    /**
     * sql sum函数
     * 例：x -> x.sum(Student::getAge, Student::getSumAge)
     * @param func 需要求和的属性 Student::getAge
     * @return SqlFunc
     */
    public final SqlFunc<T> sum(SFunction<T, ?> func) {
        String field = columnParseHandler.getField(func);
        selectFuncs.add(String.format("%s(%s) %s", SqlAggregate.SUM, fieldMapper.get(field), field));
        return this;
    }

    /**
     * sql sum函数
     * 例：x -> x.sum(Student::getAge, Student::getSumAge)
     * @param func 需要求和的属性 Student::getAge
     * @param alias 映射的别名属性 Student::getSumAge
     * @return SqlFunc
     */
    public final SqlFunc<T> sum(SFunction<T, ?> func, SFunction<T, ?> alias) {
        String field = columnParseHandler.getField(func);
        selectFuncs.add(String.format("%s(%s) %s", SqlAggregate.SUM, fieldMapper.get(field), alias));
        return this;
    }

    /**
     * sql sum函数
     * 例：x -> x.avg(Student::getAge)
     * @param func 需要求和的属性 Student::getAge
     * @return SqlFunc
     */
    public final SqlFunc<T> avg(SFunction<T, ?> func) {
        String field = columnParseHandler.getField(func);
        selectFuncs.add(String.format("%s(%s) %s", SqlAggregate.AVG, fieldMapper.get(field), field));
        return this;
    }

    /**
     * sql avg函数
     * 例：x -> x.avg(Student::getAge, Student::getSumAge)
     * @param func 需要求和的属性 Student::getAge
     * @param alias 映射的别名属性 Student::getSumAge
     * @return SqlFunc
     */
    public final SqlFunc<T> avg(SFunction<T, ?> func, SFunction<T, ?> alias) {
        String field = columnParseHandler.getField(func);
        selectFuncs.add(String.format("%s(%s) %s", SqlAggregate.AVG, fieldMapper.get(field), field));
        return this;
    }


    /**
     * sql count函数
     * @param func 实体::get属性方法
     * @param distinct 是否去重
     * @return SqlFunc
     */
    public final SqlFunc<T> count(SFunction<T, ?> func, boolean distinct) {
        String field = columnParseHandler.getField(func);
        String column = fieldMapper.get(field);
        selectFuncs.add(distinct ? String.format("%s(distinct %s) %s", SqlAggregate.COUNT, column, field) : String.format("%s(%s) %s", SqlAggregate.COUNT, column, field));
        return this;
    }

    /**
     * sql count函数
     * 例：x -> x.count(Student::getAge, true, Student::getSumAge)
     * @param func 需要求和的属性 Student::getAge
     * @param alias 映射的别名属性 Student::getSumAge
     * @return SqlFunc
     */
    public final SqlFunc<T> count(SFunction<T, ?> func, boolean distinct, SFunction<T, ?> alias) {
        String column = fieldMapper.get(columnParseHandler.getField(func));
        String aliasField = columnParseHandler.getField(func);
        selectFuncs.add(distinct ? String.format("%s(distinct %s) %s", SqlAggregate.COUNT, column, aliasField) : String.format("%s(%s) %s", SqlAggregate.COUNT, column, aliasField));
        return this;
    }

    public final SqlFunc<T> count(SFunction<T, ?> func) {
        return count(func, false);
    }
    public final SqlFunc<T> count(SFunction<T, ?> func, SFunction<T, ?> alias) {
        return count(func, false, alias);
    }

    /**
     * sql ifnull函数
     * @param func 实体::get属性方法
     * @param elseVal 为空的替代值
     * @return SqlFunc
     */
    public final SqlFunc<T> ifNull(SFunction<T, ?> func, Object elseVal) {
        String field = columnParseHandler.getField(func);
        String column = fieldMapper.get(field);
        selectFuncs.add(String.format("%s(%s, %s) %s", SqlAggregate.IFNULL, column, elseVal, field));
        return this;
    }

    /**
     * sql max函数
     * @param func 实体::get属性方法
     * @return SqlFunc
     */
    public final SqlFunc<T> max(SFunction<T, ?> func) {
        String field = columnParseHandler.getField(func);
        selectFuncs.add(String.format("%s(%s) %s", SqlAggregate.MAX, fieldMapper.get(field), field));
        return this;
    }


    /**
     * sql min函数
     * @param func 实体::get属性方法
     * @return SqlFunc
     */
    public final SqlFunc<T> min(SFunction<T, ?> func) {
        String field = columnParseHandler.getField(func);
        selectFuncs.add(String.format("%s(%s) %s", SqlAggregate.MIN, fieldMapper.get(field), field));
        return this;
    }


    protected StringJoiner getSelectFuncs() {
        return selectFuncs;
    }

    protected String getSelectColumns() {
        return selectFuncs.toString();
    }
}
