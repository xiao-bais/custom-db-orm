package com.custom.action.condition;

import com.custom.comm.SymbolConstant;
import com.custom.comm.enums.SqlAggregate;
import com.custom.comm.exceptions.CustomCheckException;

import java.lang.reflect.Field;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/15 19:55
 * @Desc：sql查询函数，方法中，column参数的字段必须要标注Db*注解，alias可不带
 **/
public class SelectFunc<T> extends AbstractSqlFunc<T, SelectFunc<T>> {

    public SelectFunc(Class<T> entityClass) {
        super.init(entityClass);
    }

    /**
     * sql sum函数
     * 例：x -> x.sum(Student::getAge)
     * @param column 需要求和的属性 Student::getAge
     * @return SqlFunc
     */
    @Override
    public final SelectFunc<T> sum(SFunction<T, ?> column) {
        String field = getColumnParseHandler().parseToField(column);
        return doFunc(formatRex(SqlAggregate.SUM), SqlAggregate.SUM, getFieldMapper().get(field), field);
    }

    @Override
    public SelectFunc<T> sum(boolean isNullToZero, SFunction<T, ?> column) {
        String field = getColumnParseHandler().parseToField(column);
        return doFunc(formatRex(SqlAggregate.SUM, isNullToZero), SqlAggregate.SUM, getFieldMapper().get(field), field);
    }

    /**
     * sql sum函数
     * 例：x -> x.sum(Student::getAge, Student::getSumAge)
     * @param column 需要求和的属性 Student::getAge
     * @param alias 映射的别名属性 Student::getSumAge
     * @return SqlFunc
     */
    public final SelectFunc<T> sum(SFunction<T, ?> column, SFunction<T, ?> alias) {
        String field = getColumnParseHandler().parseToField(column);
        String aliasField = getColumnParseHandler().parseToField(alias);
        return doFunc(formatRex(SqlAggregate.SUM), SqlAggregate.SUM, getFieldMapper().get(field), aliasField);
    }

    public final SelectFunc<T> sum(boolean isNullToZero, SFunction<T, ?> column, SFunction<T, ?> alias) {
        String field = getColumnParseHandler().parseToField(column);
        String aliasField = getColumnParseHandler().parseToField(alias);
        return doFunc(formatRex(SqlAggregate.SUM, isNullToZero), SqlAggregate.SUM, getFieldMapper().get(field), aliasField);
    }

    /**
     * sql sum函数
     * 例：x -> x.avg(Student::getAge)
     * @param column 需要求平均的属性 Student::getAge
     * @return SqlFunc
     */
    @Override
    public final SelectFunc<T> avg(SFunction<T, ?> column) {
        String field = getColumnParseHandler().parseToField(column);
        return doFunc(formatRex(SqlAggregate.AVG), SqlAggregate.AVG, getFieldMapper().get(field), field);
    }

    @Override
    public SelectFunc<T> avg(boolean isNullToZero, SFunction<T, ?> column) {
        String field = getColumnParseHandler().parseToField(column);
        return doFunc(formatRex(SqlAggregate.AVG, isNullToZero), SqlAggregate.AVG, getFieldMapper().get(field), field);
    }

    /**
     * sql avg函数
     * 例：x -> x.avg(Student::getAge, Student::getAvgAge)
     * @param column 需要求平均的属性 Student::getAge
     * @param alias 映射的别名属性 Student::getAvgAge
     * @return SqlFunc
     */
    public final SelectFunc<T> avg(SFunction<T, ?> column, SFunction<T, ?> alias) {
        String originColumn = getColumnParseHandler().parseToColumn(column);
        String aliasField = getColumnParseHandler().parseToField(alias);
        return doFunc(formatRex(SqlAggregate.AVG), SqlAggregate.AVG, originColumn, aliasField);
    }

    public final SelectFunc<T> avg(boolean isNullToZero, SFunction<T, ?> column, SFunction<T, ?> alias) {
        String originColumn = getColumnParseHandler().parseToColumn(column);
        String aliasField = getColumnParseHandler().parseToField(alias);
        return doFunc(formatRex(SqlAggregate.AVG, isNullToZero), SqlAggregate.AVG, originColumn, aliasField);
    }


    /**
     * sql count函数
     * 例：x -> x.count(Student::getAge, true)
     * @param column 实体::get属性方法 Student::getAge
     * @param distinct 是否去重 true
     * @return SqlFunc
     */
    @Override
    public final SelectFunc<T> count(SFunction<T, ?> column, boolean distinct) {
        String originColumn = getColumnParseHandler().parseToColumn(column);
        String field = getColumnMapper().get(originColumn);
        return  doFunc(formatRex(SqlAggregate.IFNULL, distinct), SqlAggregate.COUNT, originColumn, field);
    }

    /**
     * sql count函数
     * 例：x -> x.count(Student::getAge, true, Student::getCountAge)
     * @param column 需要求和的字段属性 Student::getAge
     * @param distinct 是否去重 true
     * @param alias 映射的别名属性 Student::getCountAge
     * @return SqlFunc
     */
    public final SelectFunc<T> count(SFunction<T, ?> column, boolean distinct, SFunction<T, ?> alias) {
        String originColumn = getColumnParseHandler().parseToColumn(column);
        String aliasField = getColumnParseHandler().parseToField(alias);
        return doFunc(formatRex(SqlAggregate.COUNT, distinct), SqlAggregate.COUNT, originColumn, aliasField);
    }
    public final SelectFunc<T> count(SFunction<T, ?> column, SFunction<T, ?> alias) {
        return count(column, false, alias);
    }

    /**
     * sql ifnull函数
     * 例：x -> x.ifNull(Student::getAge, 0)
     * @param column 实体::get属性方法 Student::getAge
     * @param elseVal 为空时的替代值 0
     * @return SqlFunc
     */
    @Override
    public final SelectFunc<T> ifNull(SFunction<T, ?> column, Object elseVal) {
        String originColumn = getColumnParseHandler().parseToColumn(column);
        String field = getColumnMapper().get(originColumn);
        if (elseVal instanceof CharSequence) {
            elseVal = new StringBuilder().append(SymbolConstant.SINGLE_QUOTES).append(elseVal).append(SymbolConstant.SINGLE_QUOTES);
        }
        return doFunc(formatRex(SqlAggregate.IFNULL), SqlAggregate.IFNULL, originColumn, elseVal, field);
    }

    /**
     * sql ifnull函数
     * 例：x -> x.ifNull(Student::getAge, 0, Student::getNotNullAge)
     * @param column 实体::get属性方法 Student::getAge
     * @param elseVal 为空时的替代值 0
     * @param alias 映射的别名 Student::getNotNullAge
     * @return SqlFunc
     */
    public final SelectFunc<T> ifNull(SFunction<T, ?> column, Object elseVal, SFunction<T, ?> alias) {
        String originColumn = getColumnParseHandler().parseToColumn(column);
        String aliasField = getColumnParseHandler().parseToField(alias);
        Field targetField = getColumnParseHandler().loadFields().stream()
                .filter(x -> x.getName().equals(getColumnMapper().get(originColumn)))
                .findFirst()
                .orElseThrow(() -> new CustomCheckException("未找到字段：" + getColumnMapper().get(originColumn)));
        if (targetField.getType().equals(CharSequence.class)) {
            elseVal = new StringBuilder().append(SymbolConstant.SINGLE_QUOTES).append(elseVal).append(SymbolConstant.SINGLE_QUOTES);
        }
        return doFunc(formatRex(SqlAggregate.IFNULL), SqlAggregate.IFNULL, originColumn, elseVal, aliasField);
    }

    /**
     * sql max函数
     * 例：x -> x.max(Student::getAge)
     * @param column 实体::get属性方法 Student::getAge
     * @return SqlFunc
     */
    @Override
    public final SelectFunc<T> max(SFunction<T, ?> column) {
        String originColumn = getColumnParseHandler().parseToColumn(column);
        return doFunc(formatRex(SqlAggregate.MAX), SqlAggregate.MAX, originColumn, getColumnMapper().get(originColumn));
    }

    @Override
    public SelectFunc<T> max(boolean isNullToZero, SFunction<T, ?> column) {
        String originColumn = getColumnParseHandler().parseToColumn(column);
        return doFunc(formatRex(SqlAggregate.MAX, isNullToZero), SqlAggregate.MAX, originColumn, getColumnMapper().get(originColumn));
    }

    /**
     * sql max函数
     * 例：x -> x.max(Student::getAge, Student::getMaxAge)
     * @param column 实体::get属性方法 Student::getAge
     * @param alias 映射的别名 Student::getMaxAge
     * @return SqlFunc
     */
    public final SelectFunc<T> max(SFunction<T, ?> column, SFunction<T, ?> alias) {
        String originColumn = getColumnParseHandler().parseToColumn(column);
        String aliasField = getColumnParseHandler().parseToField(alias);
        return doFunc(formatRex(SqlAggregate.MAX), SqlAggregate.MAX, originColumn, aliasField);
    }

    public final SelectFunc<T> max(boolean isNullToZero, SFunction<T, ?> column, SFunction<T, ?> alias) {
        String originColumn = getColumnParseHandler().parseToColumn(column);
        String aliasField = getColumnParseHandler().parseToField(alias);
        return doFunc(formatRex(SqlAggregate.MAX, isNullToZero), SqlAggregate.MAX, originColumn, aliasField);
    }


    /**
     * sql min函数
     * 例：x -> x.min(Student::getAge)
     * @param column 实体::get属性方法 Student::getAge
     * @return SqlFunc
     */
    @Override
    public final SelectFunc<T> min(SFunction<T, ?> column) {
        String originColumn = getColumnParseHandler().parseToColumn(column);
       return doFunc(formatRex(SqlAggregate.MIN), SqlAggregate.MIN, originColumn, getColumnMapper().get(originColumn));
    }

    @Override
    public SelectFunc<T> min(boolean isNullToZero, SFunction<T, ?> column) {
        String originColumn = getColumnParseHandler().parseToColumn(column);
        return doFunc(formatRex(SqlAggregate.MIN, isNullToZero), SqlAggregate.MIN, originColumn, getColumnMapper().get(originColumn));
    }

    /**
     * sql min函数
     * 例：x -> x.min(Student::getAge, Student::getMinAge)
     * @param column 实体::get属性方法 Student::getAge
     * @param alias 映射的别名 Student::getMinAge
     * @return SqlFunc
     */
    public final SelectFunc<T> min(SFunction<T, ?> column, SFunction<T, ?> alias) {
        String originColumn = getColumnParseHandler().parseToColumn(column);
        String aliasField = getColumnParseHandler().parseToField(alias);
        String formatRex = formatRex(SqlAggregate.MIN);
        return doFunc(formatRex, SqlAggregate.MIN, originColumn, aliasField);
    }

    public final SelectFunc<T> min(boolean isNullToZero, SFunction<T, ?> column, SFunction<T, ?> alias) {
        String originColumn = getColumnParseHandler().parseToColumn(column);
        String aliasField = getColumnParseHandler().parseToField(alias);
        return doFunc(formatRex(SqlAggregate.MIN, isNullToZero), SqlAggregate.MIN, originColumn, aliasField);
    }

}
