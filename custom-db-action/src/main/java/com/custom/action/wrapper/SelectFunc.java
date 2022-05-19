package com.custom.action.wrapper;

import com.custom.comm.SymbolConstant;
import com.custom.comm.enums.SqlAggregate;
import com.custom.comm.exceptions.CustomCheckException;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/15 19:55
 * @Desc：sql查询函数，方法中，func参数的字段必须要标注Db*注解，alias可不带
 **/
public class SelectFunc<T> extends SqlFunc<T, SelectFunc<T>> {

    public SelectFunc(Class<T> entityClass) {
        super.init(entityClass);
    }

    /**
     * sql sum函数
     * 例：x -> x.sum(Student::getAge)
     * @param func 需要求和的属性 Student::getAge
     * @return SqlFunc
     */
    @Override
    public final SelectFunc<T> sum(SFunction<T, ?> func) {
        String field = getColumnParseHandler().getField(func);
        return doFunc(formatRex(SqlAggregate.SUM, null), SqlAggregate.SUM, getFieldMapper().get(field), field);
    }

    @Override
    public SelectFunc<T> sum(boolean isNullToZero, SFunction<T, ?> func) {
        String field = getColumnParseHandler().getField(func);
        return doFunc(formatRex(SqlAggregate.SUM, isNullToZero, null), SqlAggregate.SUM, getFieldMapper().get(field), field);
    }

    /**
     * sql sum函数
     * 例：x -> x.sum(Student::getAge, Student::getSumAge)
     * @param func 需要求和的属性 Student::getAge
     * @param alias 映射的别名属性 Student::getSumAge
     * @return SqlFunc
     */
    public final SelectFunc<T> sum(SFunction<T, ?> func, SFunction<T, ?> alias) {
        String field = getColumnParseHandler().getField(func);
        String aliasField = getColumnParseHandler().getField(alias);
        return doFunc(formatRex(SqlAggregate.SUM,  null), SqlAggregate.SUM, getFieldMapper().get(field), aliasField);
    }

    /**
     * sql sum函数
     * 例：x -> x.avg(Student::getAge)
     * @param func 需要求平均的属性 Student::getAge
     * @return SqlFunc
     */
    @Override
    public final SelectFunc<T> avg(SFunction<T, ?> func) {
        String field = getColumnParseHandler().getField(func);
        return doFunc(formatRex(SqlAggregate.AVG,  null), SqlAggregate.AVG, getFieldMapper().get(field), field);
    }

    @Override
    public SelectFunc<T> avg(boolean isNullToZero, SFunction<T, ?> func) {
        String field = getColumnParseHandler().getField(func);
        return doFunc(formatRex(SqlAggregate.AVG, isNullToZero, null), SqlAggregate.AVG, getFieldMapper().get(field), field);
    }

    /**
     * sql avg函数
     * 例：x -> x.avg(Student::getAge, Student::getAvgAge)
     * @param func 需要求平均的属性 Student::getAge
     * @param alias 映射的别名属性 Student::getAvgAge
     * @return SqlFunc
     */
    public final SelectFunc<T> avg(SFunction<T, ?> func, SFunction<T, ?> alias) {
        String column = getColumnParseHandler().getColumn(func);
        String aliasField = getColumnParseHandler().getField(alias);
        return doFunc(formatRex(SqlAggregate.AVG, null), SqlAggregate.AVG, column, aliasField);
    }


    /**
     * sql count函数
     * 例：x -> x.count(Student::getAge, true)
     * @param func 实体::get属性方法 Student::getAge
     * @param distinct 是否去重 true
     * @return SqlFunc
     */
    @Override
    public final SelectFunc<T> count(SFunction<T, ?> func, boolean distinct) {
        String column = getColumnParseHandler().getColumn(func);
        String field = getColumnMapper().get(column);
        return  doFunc(formatRex(SqlAggregate.IFNULL, distinct), SqlAggregate.COUNT, column, field);
    }

    /**
     * sql count函数
     * 例：x -> x.count(Student::getAge, true, Student::getCountAge)
     * @param func 需要求和的字段属性 Student::getAge
     * @param distinct 是否去重 true
     * @param alias 映射的别名属性 Student::getCountAge
     * @return SqlFunc
     */
    public final SelectFunc<T> count(SFunction<T, ?> func, boolean distinct, SFunction<T, ?> alias) {
        String column = getColumnParseHandler().getColumn(func);
        String aliasField = getColumnParseHandler().getField(alias);
        return doFunc(formatRex(SqlAggregate.COUNT, distinct), SqlAggregate.COUNT, column, aliasField);
    }
    public final SelectFunc<T> count(SFunction<T, ?> func, SFunction<T, ?> alias) {
        return count(func, false, alias);
    }

    /**
     * sql ifnull函数
     * 例：x -> x.ifNull(Student::getAge, 0)
     * @param func 实体::get属性方法 Student::getAge
     * @param elseVal 为空时的替代值 0
     * @return SqlFunc
     */
    @Override
    public final SelectFunc<T> ifNull(SFunction<T, ?> func, Object elseVal) {
        String column = getColumnParseHandler().getColumn(func);
        String field = getColumnMapper().get(column);
        if (elseVal instanceof CharSequence) {
            elseVal = new StringBuilder().append(SymbolConstant.SINGLE_QUOTES).append(elseVal).append(SymbolConstant.SINGLE_QUOTES);
        }
        return doFunc(formatRex(SqlAggregate.IFNULL,null), SqlAggregate.IFNULL, column, elseVal, field);
    }

    /**
     * sql ifnull函数
     * 例：x -> x.ifNull(Student::getAge, 0, Student::getNotNullAge)
     * @param func 实体::get属性方法 Student::getAge
     * @param elseVal 为空时的替代值 0
     * @param alias 映射的别名 Student::getNotNullAge
     * @return SqlFunc
     */
    public final SelectFunc<T> ifNull(SFunction<T, ?> func, Object elseVal, SFunction<T, ?> alias) {
        String column = getColumnParseHandler().getColumn(func);
        String aliasField = getColumnParseHandler().getField(alias);
        Field targetField = Arrays.stream(getColumnParseHandler().getFields())
                .filter(x -> x.getName().equals(getColumnMapper().get(column)))
                .findFirst()
                .orElseThrow(() -> new CustomCheckException("未找到字段：" + getColumnMapper().get(column)));
        if (targetField.getType().equals(CharSequence.class)) {
            elseVal = new StringBuilder().append(SymbolConstant.SINGLE_QUOTES).append(elseVal).append(SymbolConstant.SINGLE_QUOTES);
        }
        return doFunc(formatRex(SqlAggregate.IFNULL, null), SqlAggregate.IFNULL, column, elseVal, aliasField);
    }

    /**
     * sql max函数
     * 例：x -> x.max(Student::getAge)
     * @param func 实体::get属性方法 Student::getAge
     * @return SqlFunc
     */
    @Override
    public final SelectFunc<T> max(SFunction<T, ?> func) {
        String column = getColumnParseHandler().getColumn(func);
        return doFunc(formatRex(SqlAggregate.MAX, null), SqlAggregate.MAX, column, getColumnMapper().get(column));
    }

    @Override
    public SelectFunc<T> max(boolean isNullToZero, SFunction<T, ?> func) {
        String column = getColumnParseHandler().getColumn(func);
        return doFunc(formatRex(SqlAggregate.MAX, isNullToZero, null), SqlAggregate.MAX, column, getColumnMapper().get(column));
    }

    /**
     * sql max函数
     * 例：x -> x.max(Student::getAge, Student::getMaxAge)
     * @param func 实体::get属性方法 Student::getAge
     * @param alias 映射的别名 Student::getMaxAge
     * @return SqlFunc
     */
    public final SelectFunc<T> max(SFunction<T, ?> func, SFunction<T, ?> alias) {
        String column = getColumnParseHandler().getColumn(func);
        String aliasField = getColumnParseHandler().getField(alias);
        return doFunc(formatRex(SqlAggregate.MAX, null), SqlAggregate.MAX, column, aliasField);
    }


    /**
     * sql min函数
     * 例：x -> x.min(Student::getAge)
     * @param func 实体::get属性方法 Student::getAge
     * @return SqlFunc
     */
    @Override
    public final SelectFunc<T> min(SFunction<T, ?> func) {
        String column = getColumnParseHandler().getColumn(func);
       return doFunc(formatRex(SqlAggregate.MIN,null), SqlAggregate.MIN, column, getColumnMapper().get(column));
    }

    @Override
    public SelectFunc<T> min(boolean isNullToZero, SFunction<T, ?> func) {
        String column = getColumnParseHandler().getColumn(func);
        return doFunc(formatRex(SqlAggregate.MIN, isNullToZero, null), SqlAggregate.MIN, column, getColumnMapper().get(column));
    }

    /**
     * sql min函数
     * 例：x -> x.min(Student::getAge, Student::getMinAge)
     * @param func 实体::get属性方法 Student::getAge
     * @param alias 映射的别名 Student::getMinAge
     * @return SqlFunc
     */
    public final SelectFunc<T> min(SFunction<T, ?> func, SFunction<T, ?> alias) {
        String field = getColumnParseHandler().getColumn(func);
        String aliasField = getColumnParseHandler().getField(alias);
       return doFunc(formatRex(SqlAggregate.MIN, null), SqlAggregate.MIN, getFieldMapper().get(field), aliasField);
    }

}
