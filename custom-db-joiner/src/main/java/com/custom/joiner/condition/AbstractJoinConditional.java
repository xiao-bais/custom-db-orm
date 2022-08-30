package com.custom.joiner.condition;

import com.custom.action.condition.DefaultColumnParseHandler;
import com.custom.action.condition.SFunction;
import com.custom.action.interfaces.ColumnParseHandler;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.sqlparser.TableSqlBuilder;
import com.custom.joiner.interfaces.DoJoin;

import java.util.Collection;

/**
 * @author Xiao-Bai
 * @date 2022/8/29 13:50
 * @desc
 */
@SuppressWarnings("all")
public abstract class AbstractJoinConditional<A, B> {

    /**
     * 关联表的别名
     */
    public abstract LambdaJoinConditional<A, B> alias(String joinAlias);

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
    private Class<A> primaryTable;
    private Class<B> joinTable;
    private String primaryTableName;
    private String primaryTableAlias;
    private String joinTableName;
    private String joinTbaleAlias;
    private ColumnParseHandler<A> primaryParserHandler;
    private ColumnParseHandler<B> joinParserHandler;
    protected LambdaJoinConditional<A, B> childrenThis = (LambdaJoinConditional<A, B>) this;


    /**
     * 条件应用
     */
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
        return joinParserHandler.parseToNormalColumn(aColumn);
    }

    protected String formatJoinCondition(String aColumn, String bColumn) {

        return null;
    }

    public AbstractJoinConditional(Class<A> aClass, Class<B> bClass) {
        this.joinCondition = new StringBuilder();
        this.primaryTable = aClass;
        this.joinTable = bClass;
        TableSqlBuilder<A> primaryModel = TableInfoCache.getTableModel(aClass);
        TableSqlBuilder<B> joinModel = TableInfoCache.getTableModel(bClass);
        this.primaryTableName = primaryModel.getTable();
        this.joinTableName = joinModel.getTable();
        this.primaryParserHandler = new DefaultColumnParseHandler<>(aClass);
        this.joinParserHandler = new DefaultColumnParseHandler<>(bClass);
    }

    public String getJoinTbaleAlias() {
        return joinTbaleAlias;
    }

    public StringBuilder getJoinCondition() {
        return joinCondition;
    }

    public String getPrimaryTableAlias() {
        return primaryTableAlias;
    }

    public ColumnParseHandler<A> getPrimaryParserHandler() {
        return primaryParserHandler;
    }

    public ColumnParseHandler<B> getJoinParserHandler() {
        return joinParserHandler;
    }

    public String getPrimaryTableName() {
        return primaryTableName;
    }

    public String getJoinTableName() {
        return joinTableName;
    }

    public void setPrimaryTableAlias(String primaryTableAlias) {
        this.primaryTableAlias = primaryTableAlias;
    }

    public void setJoinTbaleAlias(String joinTbaleAlias) {
        this.joinTbaleAlias = joinTbaleAlias;
    }
}
