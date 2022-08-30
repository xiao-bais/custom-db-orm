package com.custom.joiner.condition;

import com.custom.action.condition.DefaultColumnParseHandler;
import com.custom.action.condition.SFunction;
import com.custom.action.interfaces.ColumnParseHandler;
import com.custom.joiner.interfaces.DoJoin;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author Xiao-Bai
 * @date 2022/8/29 13:50
 * @desc
 */
@SuppressWarnings("all")
public abstract class AbstractJoinConditional<A, B> {

    public abstract LambdaJoinConditional<A, B> eq(SFunction<A, ?> aColumn, SFunction<B, ?> bColumn);

    public abstract LambdaJoinConditional<A, B> eq(SFunction<B, ?> bColumn, Object val);

    public abstract LambdaJoinConditional<A, B> gt(SFunction<B, ?> bColumn, Object val);

    public abstract LambdaJoinConditional<A, B> ge(SFunction<B, ?> bColumn, Object val);

    public abstract LambdaJoinConditional<A, B> lt(SFunction<B, ?> bColumn, Object val);

    public abstract LambdaJoinConditional<A, B> le(SFunction<B, ?> bColumn, Object val);

    public abstract LambdaJoinConditional<A, B> between(SFunction<B, ?> bColumn, Object val1, Object val2);


    public abstract LambdaJoinConditional<A, B> in(SFunction<B, ?> bColumn, Object... values);
    public abstract LambdaJoinConditional<A, B> in(SFunction<B, ?> bColumn, Collection<?> val);


    private StringBuilder joinCondition;
    private ColumnParseHandler<A> primaryParserHandler;
    private ColumnParseHandler<B> relatedParserHandler;
    protected LambdaJoinConditional<A, B> childrenThis = (LambdaJoinConditional<A, B>) this;


    protected LambdaJoinConditional<A, B> applyCondition(DoJoin doJoin) {
        String action = doJoin.action();
        if (joinCondition != null) {
            joinCondition.append(action);
        }
        return childrenThis;
    }

    protected String toAColumn(SFunction<A, ?> aColumn) {
        return primaryParserHandler.parseToNormalColumn(aColumn);
    }

    protected String toBColumn(SFunction<B, ?> aColumn) {
        return relatedParserHandler.parseToNormalColumn(aColumn);
    }

    public AbstractJoinConditional(Class<A> aClass, Class<B> bClass) {
        this.joinCondition = new StringBuilder();
        this.primaryParserHandler = new DefaultColumnParseHandler<>(aClass);
        this.relatedParserHandler = new DefaultColumnParseHandler<>(bClass);
    }
}
