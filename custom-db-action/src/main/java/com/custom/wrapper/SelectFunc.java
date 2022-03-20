package com.custom.wrapper;

import com.custom.enums.SqlAggregate;

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
        return doFunc(getFormatRex(SqlAggregate.SUM, null), SqlAggregate.SUM, getFieldMapper().get(field), field);
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
        return doFunc(getFormatRex(SqlAggregate.SUM,  null), SqlAggregate.SUM, getFieldMapper().get(field), alias);
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
        return doFunc(getFormatRex(SqlAggregate.AVG,  null), SqlAggregate.AVG, getFieldMapper().get(field), field);
    }

    /**
     * sql avg函数
     * 例：x -> x.avg(Student::getAge, Student::getAvgAge)
     * @param func 需要求平均的属性 Student::getAge
     * @param alias 映射的别名属性 Student::getAvgAge
     * @return SqlFunc
     */
    public final SelectFunc<T> avg(SFunction<T, ?> func, SFunction<T, ?> alias) {
        String field = getColumnParseHandler().getField(func);
        String aliasField = getColumnParseHandler().getField(alias);
        return doFunc(getFormatRex(SqlAggregate.AVG, null), SqlAggregate.AVG, getFieldMapper().get(field), aliasField);
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
        String field = getColumnParseHandler().getField(func);
        String column = getFieldMapper().get(field);
        return  doFunc(getFormatRex(SqlAggregate.IF_NULL, distinct), SqlAggregate.COUNT, column, field);
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
        String column = getFieldMapper().get(getColumnParseHandler().getField(func));
        String aliasField = getColumnParseHandler().getField(alias);
        return doFunc(getFormatRex(SqlAggregate.COUNT, distinct), SqlAggregate.COUNT, column, aliasField);
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
        String field = getColumnParseHandler().getField(func);
        String column = getFieldMapper().get(field);
        return doFunc(getFormatRex(SqlAggregate.IF_NULL,null), SqlAggregate.IF_NULL, column, elseVal, field);
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
        String column = getFieldMapper().get(getColumnParseHandler().getField(func));
        String aliasField = getColumnParseHandler().getField(alias);
        return doFunc(getFormatRex(SqlAggregate.IF_NULL, null), SqlAggregate.IF_NULL, column, elseVal, aliasField);
    }

    /**
     * sql max函数
     * 例：x -> x.max(Student::getAge)
     * @param func 实体::get属性方法 Student::getAge
     * @return SqlFunc
     */
    @Override
    public final SelectFunc<T> max(SFunction<T, ?> func) {
        String field = getColumnParseHandler().getField(func);
        return doFunc(getFormatRex(SqlAggregate.MAX, null), SqlAggregate.MAX, getFieldMapper().get(field), field);
    }

    /**
     * sql max函数
     * 例：x -> x.max(Student::getAge, Student::getMaxAge)
     * @param func 实体::get属性方法 Student::getAge
     * @param alias 映射的别名 Student::getMaxAge
     * @return SqlFunc
     */
    public final SelectFunc<T> max(SFunction<T, ?> func, SFunction<T, ?> alias) {
        String field = getColumnParseHandler().getField(func);
        String aliasField = getColumnParseHandler().getField(alias);
        return doFunc(getFormatRex(SqlAggregate.MAX, null), SqlAggregate.MAX, getFieldMapper().get(field), aliasField);
    }


    /**
     * sql min函数
     * 例：x -> x.min(Student::getAge)
     * @param func 实体::get属性方法 Student::getAge
     * @return SqlFunc
     */
    @Override
    public final SelectFunc<T> min(SFunction<T, ?> func) {
        String field = getColumnParseHandler().getField(func);
       return doFunc(getFormatRex(SqlAggregate.MIN,null), SqlAggregate.MIN, getFieldMapper().get(field), field);
    }

    /**
     * sql min函数
     * 例：x -> x.min(Student::getAge, Student::getMinAge)
     * @param func 实体::get属性方法 Student::getAge
     * @param alias 映射的别名 Student::getMinAge
     * @return SqlFunc
     */
    public final SelectFunc<T> min(SFunction<T, ?> func, SFunction<T, ?> alias) {
        String field = getColumnParseHandler().getField(func);
        String aliasField = getColumnParseHandler().getField(alias);
       return doFunc(getFormatRex(SqlAggregate.MIN, null), SqlAggregate.MIN, getFieldMapper().get(field), aliasField);
    }

}
