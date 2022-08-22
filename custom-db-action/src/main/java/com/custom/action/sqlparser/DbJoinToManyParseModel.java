package com.custom.action.sqlparser;

import com.custom.action.dbaction.AbstractJoinToResult;
import com.custom.action.util.DbUtil;
import com.custom.comm.JudgeUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.annotations.DbKey;
import com.custom.comm.annotations.DbOneToMany;
import com.custom.comm.annotations.DbOneToOne;
import com.custom.comm.enums.DbSymbol;
import com.custom.comm.exceptions.ExThrowsUtil;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Xiao-Bai
 * @date 2022/8/22 1:18
 * @desc
 */
public class DbJoinToManyParseModel extends AbstractJoinToResult {

    /**
     * 该列表是否以升序的方式来排序
     * <br/> 若{@link DbOneToMany#sortField()} 指定了排序字段，则以该字段来进行列表的升降序排列
     * <br/> 若{@link DbOneToMany#sortField()} 未指定排序字段，则默认以关联的对象主键{@link DbKey} 排序(前提是被关联的对象存在主键)
     */
    private final boolean orderByAsc;

    /**
     * 指定排序的字段，同时对上面的orderByAsc起作用(java属性即可)
     */
    private final String sortField;


    public DbJoinToManyParseModel(Field joinToManyField) {
        DbOneToMany oneToMany = joinToManyField.getAnnotation(DbOneToMany.class);
        this.orderByAsc = oneToMany.orderByAsc();
        this.sortField = oneToMany.sortField();
        super.setThisField(oneToMany.thisField());
        super.setJoinField(oneToMany.joinField());

        if (Map.class.isAssignableFrom(oneToMany.joinTarget())
                || Map.class.isAssignableFrom(joinToManyField.getType())) {
            ExThrowsUtil.toUnSupport("In '%s', @DbOneToMany.joinTarget or field type does not support working on java.util.Map", joinToManyField.toString());
        }

        // 主表
        setThisClass(joinToManyField.getDeclaringClass());
        // 关联对象不是Object类时，去给定的目标类
        if (!Object.class.equals(oneToMany.joinTarget())) {
            setJoinTarget(oneToMany.joinTarget());

        }else {
            // 否则取关联字段的类型
            Class<?> joinTarget = joinToManyField.getType();
            if (Object.class.equals(joinTarget)) {
                ExThrowsUtil.toCustom("The entity type associated in %s DbOneToOne.joinTarget() is not specified"
                        , joinToManyField.getDeclaringClass());
            }
            setJoinTarget(joinTarget);
        }
        super.initJoinProperty();

    }

    public String orderByField() {
        if (JudgeUtil.isBlank(sortField)) {
            return null;
        }
        return SymbolConstant.ORDER_BY
                + DbUtil.fullSqlColumn(getJoinAlias(), sortField)
                + (orderByAsc ? SymbolConstant.ASC : SymbolConstant.DESC);
    }

}
