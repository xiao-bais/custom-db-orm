package com.custom.joiner.core.func;

import com.custom.action.condition.SFunction;
import com.custom.action.sqlparser.ColumnPropertyMap;
import com.custom.joiner.interfaces.DoSelecting;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2022/9/1 0001 11:05
 * 常用sql函数
 */
public abstract class AbstractJoinFunction<Children extends AbstractJoinFunction<Children>> {

    /**
     * sql sum函数
     * 例：x -> x.sum(Student::getAge)
     * @param column 需要求和的属性 Student::getAge
     * @param isNullToZero 若计算结果为null, 返回0
     * @return SqlFunc
     */
    public abstract <A> Children sum(boolean isNullToZero, SFunction<A, ?> column);
    public <A> Children sum(SFunction<A, ?> column) {
        return sum(false, column);
    }

    /**
     * sql sum函数
     * 例：x -> x.avg(Student::getAge)
     * @param column 需要求平均的属性 Student::getAge
     * @param isNullToZero 若计算结果为null, 返回0
     * @return SqlFunc
     */
    public abstract <A> Children avg(boolean isNullToZero, SFunction<A, ?> column);
    public <A> Children avg(SFunction<A, ?> column) {
        return avg(false, column);
    }


    /**
     * sql count函数
     * 例：x -> x.count(Student::getAge, true, Student::getCountAge)
     * @param column 需要求和的字段属性 Student::getAge
     * @param distinct 是否去重？
     * @return SqlFunc
     */
    public abstract <R> Children count(SFunction<R, ?> column, boolean distinct);
    public <R> Children count(SFunction<R, ?> column) {
        return count(column, false);
    }

    /**
     * sql ifnull函数
     * 例：x -> x.ifNull(Student::getAge, 0)
     * @param column 实体::get属性方法 Student::getAge
     * @param elseVal 为空时的替代值
     * @return SqlFunc
     */
    public abstract <A> Children ifNull(SFunction<A, ?> column, Object elseVal);

    /**
     * sql max函数
     * 例：x -> x.max(Student::getAge)
     * @param column 实体::get属性方法 Student::getAge
     * @param isNullToZero 若计算结果为null, 返回0
     * @return SqlFunc
     */
    public abstract <A> Children max(boolean isNullToZero, SFunction<A, ?> column);
    public <A> Children max(SFunction<A, ?> column) {
        return max(false, column);
    }


    /**
     * sql min函数
     * 例：x -> x.min(Student::getAge)
     * @param column 实体::get属性方法 Student::getAge
     * @return SqlFunc
     */
    public abstract <A> Children min(boolean isNullToZero, SFunction<A, ?> column);
    public <A> Children min(SFunction<A, ?> column) {
        return min(false, column);
    }



    /* -------------------------------------------------------------------------------------------------------------------------------------------------*/

    /**
     * lambda函数解析列表
     */
    private List<ColumnPropertyMap<?>> columnPropertyMaps;
    /**
     * sql查询字段
     *
     */
    List<DoSelecting> selectingList;

    public AbstractJoinFunction() {
        selectingList = new ArrayList<>();
    }


    public void doSelect(DoSelecting selecting) {
        this.selectingList.add(selecting);
    }



}
