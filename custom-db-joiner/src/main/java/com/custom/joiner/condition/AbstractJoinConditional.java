package com.custom.joiner.condition;

import com.custom.action.condition.DefaultColumnParseHandler;
import com.custom.action.condition.SFunction;
import com.custom.action.interfaces.ColumnParseHandler;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.sqlparser.TableSqlBuilder;
import com.custom.action.util.LambdaResolveUtil;
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
public abstract class AbstractJoinConditional<T, A> {

    /**
     * 关联表的别名
     */
    public abstract LambdaJoinConditional<T, A> alias(String joinAlias);

    public abstract LambdaJoinConditional<T, A> eq(SFunction<A, ?> aColumn, SFunction<T, ?> bColumn);

    public abstract LambdaJoinConditional<T, A> eq(SFunction<T, ?> bColumn, Object val);

    public abstract LambdaJoinConditional<T, A> gt(SFunction<T, ?> bColumn, Object val);

    public abstract LambdaJoinConditional<T, A> ge(SFunction<T, ?> bColumn, Object val);

    public abstract LambdaJoinConditional<T, A> lt(SFunction<T, ?> bColumn, Object val);

    public abstract LambdaJoinConditional<T, A> le(SFunction<T, ?> bColumn, Object val);

    public abstract LambdaJoinConditional<T, A> between(SFunction<T, ?> bColumn, Object val1, Object val2);


    public abstract LambdaJoinConditional<T, A> in(SFunction<T, ?> bColumn, Object... values);
    public abstract LambdaJoinConditional<T, A> in(SFunction<T, ?> bColumn, Collection<?> val);


    private StringBuilder joinCondition;
    private Class<T> joinClass;
    private String joinTableName;
    private String joinTbaleAlias;
    private List<DoJoin> joinList;
    private ColumnParseHandler<T> joinParserHandler;

    private Class<A> primaryClass;
    private ColumnParseHandler<A> primaryParserHandler;
    private String primaryTableName;
    private String primaryTableAlias;
    protected LambdaJoinConditional<T, A> childrenThis = (LambdaJoinConditional<T, A>) this;


    /**
     * 条件应用
     */
    protected LambdaJoinConditional<T, A> applyCondition(DoJoin doJoin) {
        if (doJoin != null) {
            joinList.add(doJoin);
        }
        return childrenThis;
    }

    protected <A> String toAColumn(SFunction<A, ?> aColumn) {
        if (primaryParserHandler == null) {
            Class<A> implClass = LambdaResolveUtil.getImplClass(aColumn);
            this.primaryParserHandler = new DefaultColumnParseHandler<>(implClass);
        }
        return primaryParserHandler.parseToNormalColumn(aColumn);
    }

    protected String toBColumn(SFunction<T, ?> aColumn) {
        return joinParserHandler.parseToNormalColumn(aColumn);
    }

    protected String formatJoinCondition(String aColumn, String bColumn) {
        return String.format("%s.%s = %s.%s",
                this.primaryTableAlias, aColumn,
                this.joinTbaleAlias, bColumn);
    }

    public AbstractJoinConditional(Class<T> bClass) {
        this.joinCondition = new StringBuilder();
        this.joinList = new ArrayList<>();
        this.joinClass = bClass;
        TableSqlBuilder<T> joinModel = TableInfoCache.getTableModel(bClass);
        this.joinTableName = joinModel.getTable();
        this.joinParserHandler = new DefaultColumnParseHandler<>(bClass);
    }

    public void setPrimaryTableInfo(Class<A> aClass, ColumnParseHandler<A> primaryParserHandler) {
        this.primaryClass = aClass;
        TableSqlBuilder<A> primaryModel = TableInfoCache.getTableModel(aClass);
        this.primaryTableName = primaryModel.getTable();
        this.primaryParserHandler = primaryParserHandler;
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

    public ColumnParseHandler<T> getJoinParserHandler() {
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

    public Class<A> getPrimaryClass() {
        return primaryClass;
    }

    public Class<T> getJoinClass() {
        return joinClass;
    }
}
