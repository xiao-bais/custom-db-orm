package com.custom.joiner.core;

import com.custom.action.condition.DefaultColumnParseHandler;
import com.custom.action.condition.SFunction;
import com.custom.action.interfaces.ColumnParseHandler;
import com.custom.action.sqlparser.ColumnPropertyMap;
import com.custom.action.util.DbUtil;
import com.custom.action.util.LambdaResolveUtil;
import com.custom.comm.enums.DbJoinStyle;
import com.custom.comm.enums.DbSymbol;
import com.custom.comm.enums.AliasStrategy;
import com.custom.comm.exceptions.CustomCheckException;
import com.custom.comm.utils.*;
import com.custom.joiner.interfaces.DoJoin;
import com.custom.joiner.util.CustomCharUtil;

import java.util.*;

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
    public LambdaJoinConditional<T> as(String joinAlias) {
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

    /**
     * 关联条件拼接
     */
    private StringBuilder joinConditional;
    /**
     * 关联参数
     */
    private List<Object> joinParamList;
    /**
     * 关联对象
     */
    private Class<T> joinClass;
    /**
     * 关联对象别名
     */
    private String joinTableAlias;
    /**
     * 主关联表的别名
     */
    private String primaryTableAlias;

    /**
     * lambda字段解析对象(关联对象)
     */
    private ColumnParseHandler<T> joinParserHandler;

    private ColumnPropertyMap<T> joinPropertyMap;

    private List<DoJoin> joinList;
    /**
     * 默认左连接
     */
    private DbJoinStyle joinStyle = DbJoinStyle.LEFT;
    private ColumnPropertyMap<?> primaryPropertyMap;

    protected LambdaJoinConditional<T> childrenThis = (LambdaJoinConditional<T>) this;

    /**
     * 最终的关联条件拼接
     */
    public String formatJoinSqlAction() {
        if (joinList.isEmpty()) {
            throw new CustomCheckException("未指定关联条件");
        }

        if (StrUtils.isBlank(this.joinTableAlias)
                || StrUtils.isBlank(this.primaryTableAlias)) {
            throw new UnsupportedOperationException("Viewing is not supported before the association is completed");
        }

        if (JudgeUtil.isEmpty(joinConditional)) {
            StringBuilder sqlJoinAction = new StringBuilder();
            this.joinList.forEach(join -> sqlJoinAction.append(join.action()));
            this.joinConditional.append(joinStyle.getStyle())
                    .append(String.format(" %s %s on ", this.joinPropertyMap.getTableName(), this.joinTableAlias))
                    .append(DbUtil.trimFirstAndBySqlCondition(sqlJoinAction.toString()));
        }

        return joinConditional.toString();

    }

    /**
     * 解析连接条件
     */
    protected <A> void resloveColumn(SFunction<T, ?> joinColumn, SFunction<A, ?> aColumn) {
        Asserts.notNull(joinColumn);
        String joinMethodName = LambdaResolveUtil.getImplMethodName(joinColumn);
        ColumnPropertyMap<T> tColumnPropertyMap = (ColumnPropertyMap<T>) ColumnPropertyMap.parse2Map(this.joinClass, joinMethodName);

        if (joinPropertyMap == null) {
            this.joinPropertyMap = tColumnPropertyMap;
        }

        Asserts.notNull(aColumn);
        Class<A> implClass = LambdaResolveUtil.getImplClass(aColumn);
        String aMethodName = LambdaResolveUtil.getImplMethodName(aColumn);
        ColumnPropertyMap<?> aColumnPropertyMap = ColumnPropertyMap.parse2Map(implClass, aMethodName);

        if (primaryPropertyMap == null) {
            this.primaryPropertyMap = aColumnPropertyMap;
        }

        this.addCondition(() -> String.format(" and %s.%s = %s.%s",
                this.joinTableAlias, tColumnPropertyMap.getColumn(),
                this.primaryTableAlias, aColumnPropertyMap.getColumn()
        ), null);
    }

    protected LambdaJoinConditional<T> addCondition(SFunction<T, ?> column, DbSymbol dbSymbol, Object param) {
        String implMethodName = LambdaResolveUtil.getImplMethodName(column);
        ColumnPropertyMap<T> tColumnPropertyMap = (ColumnPropertyMap<T>) ColumnPropertyMap.parse2Map(this.joinClass, implMethodName);
        String ConditionColumn = DbUtil.fullSqlColumn(this.joinTableAlias, tColumnPropertyMap.getColumn());

        DoJoin doJoin = null;
        switch (dbSymbol) {
            case EQUALS:
            case LESS_THAN:
            case LESS_THAN_EQUALS:
            case GREATER_THAN:
            case GREATER_THAN_EQUALS:
                doJoin = () -> {
                    return String.format(" AND %s %s ?", ConditionColumn, dbSymbol.getSymbol(), Constants.QUEST);
                };
                break;
            case IN:
                StringJoiner addSymbol = new StringJoiner(Constants.SEPARATOR_COMMA_2);
                if (param instanceof Collection) {
                    ((Collection<Object>) param).forEach(op -> addSymbol.add(Constants.QUEST));
                    doJoin = () -> {
                        return String.format(" AND %s %s (%s)", ConditionColumn, dbSymbol.getSymbol(), addSymbol);
                    };
                }
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return addCondition(doJoin, param);
    }

    /**
     * 添加关联条件
     */
    protected LambdaJoinConditional<T> addCondition(DoJoin doJoin, Object param) {
        this.joinList.add(doJoin);
        if (param != null) {
            CustomUtil.addParams(this.joinParamList, param);
        }
        return childrenThis;
    }


    public AbstractJoinConditional(Class<T> joinClass) {
        this.joinConditional = new StringBuilder();
        this.joinParamList = new ArrayList<>();
        this.joinList = new ArrayList<>();
        this.joinClass = joinClass;
        this.joinParserHandler = new DefaultColumnParseHandler<>(joinClass);
    }

    public Class<T> getJoinClass() {
        return joinClass;
    }

    protected ColumnPropertyMap<T> getJoinPropertyMap() {
        return joinPropertyMap;
    }

    protected ColumnPropertyMap<?> getPrimaryPropertyMap() {
        return primaryPropertyMap;
    }

    public String getJoinTableAlias() {
        return joinTableAlias;
    }

    protected void setPrimaryTableAlias(String primaryTableAlias) {
        this.primaryTableAlias = primaryTableAlias;
    }

    protected void setJoinInfo(DbJoinStyle joinStyle, String primaryAlias, AliasStrategy aliasStrategy) {
        this.joinStyle = joinStyle;
        this.primaryTableAlias = primaryAlias;
        if (StrUtils.isNotBlank(this.joinTableAlias)) {
            return;
        }

        // 设定别名
        switch (aliasStrategy) {
            case INPUT:
                Asserts.notEmpty(this.joinTableAlias,
                        "未定义表别名: "  + this.joinClass.getName());
                break;
            case UNIQUE_ID:
            default:
                this.joinTableAlias = "a_" + CustomCharUtil.nextStr(8);
                break;
            case FIRST_APPEND:
                this.joinTableAlias = CustomUtil.firstTableName(this.joinPropertyMap.getTableName());
        }
    }

    public List<Object> thisParamsList() {
        return this.joinParamList;
    }
}
