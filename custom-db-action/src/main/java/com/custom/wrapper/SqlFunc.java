package com.custom.wrapper;

import com.custom.dbconfig.SymbolConst;
import com.custom.enums.SqlAggregate;
import com.custom.sqlparser.TableInfoCache;

import java.util.Map;
import java.util.StringJoiner;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/15 19:55
 * @Desc：sql查询函数，方法中，func参数的字段必须要标注Db*注解，alias可不带
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
     * 例：x -> x.sum(Student::getAge)
     * @param func 需要求和的属性 Student::getAge
     * @return SqlFunc
     */
    public final SqlFunc<T> sum(SFunction<T, ?> func) {
        String field = columnParseHandler.getField(func);
        return doFunc("%s(%s) %s", SqlAggregate.SUM, fieldMapper.get(field), field);
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
        return doFunc("%s(%s) %s", SqlAggregate.SUM, fieldMapper.get(field), alias);
    }

    /**
     * sql sum函数
     * 例：x -> x.avg(Student::getAge)
     * @param func 需要求平均的属性 Student::getAge
     * @return SqlFunc
     */
    public final SqlFunc<T> avg(SFunction<T, ?> func) {
        String field = columnParseHandler.getField(func);
        return doFunc("%s(%s) %s", SqlAggregate.AVG, fieldMapper.get(field), field);
    }

    /**
     * sql avg函数
     * 例：x -> x.avg(Student::getAge, Student::getAvgAge)
     * @param func 需要求平均的属性 Student::getAge
     * @param alias 映射的别名属性 Student::getAvgAge
     * @return SqlFunc
     */
    public final SqlFunc<T> avg(SFunction<T, ?> func, SFunction<T, ?> alias) {
        String field = columnParseHandler.getField(func);
        String aliasField = columnParseHandler.getField(alias);
        return doFunc("%s(%s) %s", SqlAggregate.AVG, fieldMapper.get(field), aliasField);
    }


    /**
     * sql count函数
     * 例：x -> x.count(Student::getAge, true)
     * @param func 实体::get属性方法 Student::getAge
     * @param distinct 是否去重 true
     * @return SqlFunc
     */
    public final SqlFunc<T> count(SFunction<T, ?> func, boolean distinct) {
        String field = columnParseHandler.getField(func);
        String column = fieldMapper.get(field);
        return distinct ? doFunc("%s(distinct %s) %s", SqlAggregate.COUNT, column, field) : doFunc("%s(%s) %s", SqlAggregate.COUNT, column, field);
    }

    /**
     * sql count函数
     * 例：x -> x.count(Student::getAge, true, Student::getCountAge)
     * @param func 需要求和的字段属性 Student::getAge
     * @param distinct 是否去重 true
     * @param alias 映射的别名属性 Student::getCountAge
     * @return SqlFunc
     */
    public final SqlFunc<T> count(SFunction<T, ?> func, boolean distinct, SFunction<T, ?> alias) {
        String column = fieldMapper.get(columnParseHandler.getField(func));
        String aliasField = columnParseHandler.getField(alias);
        return distinct ? doFunc("%s(distinct %s) %s", SqlAggregate.COUNT, column, aliasField) : doFunc("%s(%s) %s", SqlAggregate.COUNT, column, aliasField);
    }

    public final SqlFunc<T> count(SFunction<T, ?> func) {
        return count(func, false);
    }
    public final SqlFunc<T> count(SFunction<T, ?> func, SFunction<T, ?> alias) {
        return count(func, false, alias);
    }

    /**
     * sql ifnull函数
     * 例：x -> x.ifNull(Student::getAge, 0)
     * @param func 实体::get属性方法 Student::getAge
     * @param elseVal 为空时的替代值 0
     * @return SqlFunc
     */
    public final SqlFunc<T> ifNull(SFunction<T, ?> func, Object elseVal) {
        String field = columnParseHandler.getField(func);
        String column = fieldMapper.get(field);
        return doFunc("%s(%s, %s) %s", SqlAggregate.IF_NULL, column, elseVal, field);
    }

    /**
     * sql ifnull函数
     * 例：x -> x.ifNull(Student::getAge, 0, Student::getNotNullAge)
     * @param func 实体::get属性方法 Student::getAge
     * @param elseVal 为空时的替代值 0
     * @param alias 映射的别名 Student::getNotNullAge
     * @return SqlFunc
     */
    public final SqlFunc<T> ifNull(SFunction<T, ?> func, Object elseVal, SFunction<T, ?> alias) {
        String column = fieldMapper.get(columnParseHandler.getField(func));
        String aliasField = columnParseHandler.getField(alias);
        return doFunc("%s(%s, %s) %s", SqlAggregate.IF_NULL, column, elseVal, aliasField);
    }

    /**
     * sql max函数
     * 例：x -> x.max(Student::getAge)
     * @param func 实体::get属性方法 Student::getAge
     * @return SqlFunc
     */
    public final SqlFunc<T> max(SFunction<T, ?> func) {
        String field = columnParseHandler.getField(func);
        return doFunc("%s(%s) %s", SqlAggregate.MAX, fieldMapper.get(field), field);
    }

    /**
     * sql max函数
     * 例：x -> x.max(Student::getAge, Student::getMaxAge)
     * @param func 实体::get属性方法 Student::getAge
     * @param alias 映射的别名 Student::getMaxAge
     * @return SqlFunc
     */
    public final SqlFunc<T> max(SFunction<T, ?> func, SFunction<T, ?> alias) {
        String field = columnParseHandler.getField(func);
        String aliasField = columnParseHandler.getField(alias);
        return doFunc("%s(%s) %s", SqlAggregate.MAX, fieldMapper.get(field), aliasField);
    }


    /**
     * sql min函数
     * 例：x -> x.min(Student::getAge)
     * @param func 实体::get属性方法 Student::getAge
     * @return SqlFunc
     */
    public final SqlFunc<T> min(SFunction<T, ?> func) {
        String field = columnParseHandler.getField(func);
       return doFunc("%s(%s) %s", SqlAggregate.MIN, fieldMapper.get(field), field);
    }

    /**
     * sql min函数
     * 例：x -> x.min(Student::getAge, Student::getMinAge)
     * @param func 实体::get属性方法 Student::getAge
     * @param alias 映射的别名 Student::getMinAge
     * @return SqlFunc
     */
    public final SqlFunc<T> min(SFunction<T, ?> func, SFunction<T, ?> alias) {
        String field = columnParseHandler.getField(func);
        String aliasField = columnParseHandler.getField(alias);
       return doFunc("%s(%s) %s", SqlAggregate.MIN, fieldMapper.get(field), aliasField);
    }


    /**
     * 适配函数的拼接
     * @param format 格式化的函数
     * @param params 参数
     * @return SqlFunc
     */
    private SqlFunc<T> doFunc(String format, Object... params) {
        selectFuncs.add(String.format(format, params));
        return this;
    }

    protected StringJoiner getSelectFuncs() {
        return selectFuncs;
    }

    protected String getSelectColumns() {
        return selectFuncs.toString();
    }
}
