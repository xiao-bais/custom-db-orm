package com.custom.joiner.condition;

import com.custom.action.condition.Conditions;
import com.custom.action.condition.DefaultColumnParseHandler;
import com.custom.action.condition.LambdaConditionWrapper;
import com.custom.action.condition.SFunction;
import com.custom.action.interfaces.ColumnParseHandler;
import com.custom.action.sqlparser.ColumnPropertyMap;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.util.LambdaResolveUtil;
import com.custom.comm.Asserts;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.joiner.interfaces.DoJoin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Xiao-Bai
 * @date 2022/8/29 13:50
 * @desc
 */
@SuppressWarnings("all")
public abstract class AbstractJoinConditional<T> {

    /**
     * 关联表的别名
     */
    public LambdaJoinConditional<T> alias(String joinAlias) {
        this.joinTableAlias = joinAlias;
        return childrenThis;
    }

    public abstract <A> LambdaJoinConditional<T> eq(SFunction<T, ?> joinColumn, SFunction<A, ?> aColumn);

    public abstract LambdaJoinConditional<T> eq(SFunction<T, ?> joinColumn, Object val);

    public abstract LambdaJoinConditional<T> gt(SFunction<T, ?> joinColumn, Object val);

    public abstract LambdaJoinConditional<T> ge(SFunction<T, ?> joinColumn, Object val);

    public abstract LambdaJoinConditional<T> lt(SFunction<T, ?> joinColumn, Object val);

    public abstract LambdaJoinConditional<T> le(SFunction<T, ?> joinColumn, Object val);

    public abstract LambdaJoinConditional<T> between(SFunction<T, ?> joinColumn, Object val1, Object val2);


    public abstract LambdaJoinConditional<T> in(SFunction<T, ?> joinColumn, Object... values);
    public abstract LambdaJoinConditional<T> in(SFunction<T, ?> joinColumn, Collection<?> val);


    private Class<T> joinClass;
    private String joinTableAlias;
    protected boolean isReslove = false;
    private ColumnParseHandler<T> joinParserHandler;
    private ColumnPropertyMap<T> joinPropertyMap;
    private LambdaConditionWrapper<T> conditionWrapper;

    private ColumnPropertyMap<?> primaryPropertyMap;
    protected LambdaJoinConditional<T> childrenThis = (LambdaJoinConditional<T>) this;

    /**
     * 条件拼接
     */
    public String joinConditional() {
        if (!this.isReslove) {
            ExThrowsUtil.toIllegal("关联的字段为必填: joinColumn is null or aColumn is null");
        }
        return conditionWrapper.getFinalConditional();
    }


    protected <A> void resloveColumn(SFunction<T, ?> joinColumn, SFunction<A, ?> aColumn) {
        Asserts.notNull(joinColumn);
        String joinMethodName = LambdaResolveUtil.getImplMethodName(joinColumn);
        this.joinPropertyMap = (ColumnPropertyMap<T>) ColumnPropertyMap.parse2Map(this.joinClass, joinMethodName);

        Asserts.notNull(aColumn);
        Class<A> implClass = LambdaResolveUtil.getImplClass(aColumn);
        String aMethodName = LambdaResolveUtil.getImplMethodName(aColumn);
        this.primaryPropertyMap = ColumnPropertyMap.parse2Map(implClass, aMethodName);
    }

    public AbstractJoinConditional(Class<T> joinClass) {
        this.joinClass = joinClass;
        this.joinParserHandler = new DefaultColumnParseHandler<>(joinClass);
        this.conditionWrapper = Conditions.lambdaQuery(joinClass);
    }

    public Class<T> getJoinClass() {
        return joinClass;
    }

    public ColumnPropertyMap<T> getJoinPropertyMap() {
        return joinPropertyMap;
    }

    public ColumnPropertyMap<?> getPrimaryPropertyMap() {
        return primaryPropertyMap;
    }

    public String getJoinTableAlias() {
        return joinTableAlias;
    }

    public LambdaConditionWrapper<T> thisConditionWrapper() {
        return this.conditionWrapper;
    }
}
