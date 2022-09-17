package com.custom.action.sqlparser;

import com.custom.action.dbaction.AbstractJoinToResult;
import com.custom.comm.JudgeUtil;
import com.custom.comm.annotations.DbOneToOne;
import com.custom.comm.exceptions.ExThrowsUtil;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Xiao-Bai
 * @date 2022/8/21 2:00
 * @desc 用于DbOneToOne注解的解析对象
 */
public class DbJoinToOneParseModel extends AbstractJoinToResult {

    /**
     * 若在一对一查询时，查询到不止一条数据的情况下，是否抛出异常
     * <br/> true - 是
     * <br/> false - 否，取多条中的第一条
     */
    private final boolean isThrowErr;

    /**
     * 构造方法
     * @param joinToOneField 一对一的字段
     */
    public DbJoinToOneParseModel(Field joinToOneField) {

        DbOneToOne oneToOne = joinToOneField.getAnnotation(DbOneToOne.class);
        this.isThrowErr = oneToOne.isThrowErr();
        super.setThisField(oneToOne.thisField());
        super.setJoinField(oneToOne.joinField());

        // 主表
        setThisClass(joinToOneField.getDeclaringClass());
        // 关联对象不是Object类时，去给定的目标类
        if (!Object.class.equals(oneToOne.joinTarget())) {
            setJoinTarget(oneToOne.joinTarget());

        }else {
            // 否则取关联字段的类型
            Class<?> joinTarget = joinToOneField.getType();
            if (Object.class.equals(joinTarget) || Map.class.isAssignableFrom(joinTarget)) {
                ExThrowsUtil.toCustom("The entity type associated in %s DbOneToOne.joinTarget() is not specified"
                        , joinToOneField.getDeclaringClass());
            }
            setJoinTarget(joinTarget);
        }
        super.initJoinProperty();

        // 若存在两个对象之间存在相互引用一对一注解的关系，则抛出异常
        if (TableInfoCache.existCrossReference(getThisClass(), getJoinTarget())) {
            ExThrowsUtil.toIllegal("Wrong reference. One to one annotation is not allowed to act on the mutual reference relationship between two objects in [%s] and [%s.%s] ",
                    getJoinTarget(), getThisClass(), joinToOneField.getName()
            );
        }

    }

    public boolean isThrowErr() {
        return isThrowErr;
    }

    @Override
    public String queryCondition() {
        return String.format("and %s.%s = ? ", getJoinAlias(), getJoinColumn());
    }
}
