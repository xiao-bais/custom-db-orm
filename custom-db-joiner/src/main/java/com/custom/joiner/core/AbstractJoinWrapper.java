package com.custom.joiner.core;

import com.custom.action.condition.DefaultColumnParseHandler;
import com.custom.action.interfaces.ColumnParseHandler;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.sqlparser.TableSqlBuilder;
import com.custom.comm.Asserts;
import com.custom.comm.JudgeUtil;
import com.custom.comm.enums.DbJoinStyle;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.joiner.core.condition.LambdaJoinConditionWrapper;
import com.custom.comm.enums.AliasStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @Author Xiao-Bai
 * @Date 2022/8/30 0030 16:35
 * @Desc
 */
@SuppressWarnings("unchecked")
public class AbstractJoinWrapper<T> extends LambdaJoinConditionWrapper<T> {

    private final Class<T> thisClass;
    private final String primaryTableName;
    private final String primaryTableAlias;
    private AliasStrategy aliasStrategy = AliasStrategy.UNIQUE_ID;
    private final ColumnParseHandler<T> thisColumnParseHandler;
    private final List<AbstractJoinConditional<?>> joinTableList;
    private final List<String> aliasList;
    protected LambdaJoinWrapper<T> childrenThis = (LambdaJoinWrapper<T>) this;


    protected AbstractJoinWrapper(Class<T> thisClass) {
        this.thisClass = thisClass;
        this.thisColumnParseHandler = new DefaultColumnParseHandler<>(thisClass);
        TableSqlBuilder<T> tableModel = TableInfoCache.getTableModel(this.thisClass);
        this.primaryTableName = tableModel.getTable();
        this.primaryTableAlias = tableModel.getAlias();
        this.joinTableList = new ArrayList<>();
        this.aliasList = new ArrayList<>();
    }

    protected <R> LambdaJoinWrapper<T> addJoinTable(DbJoinStyle joinStyle, Class<R> joinClass, Consumer<AbstractJoinConditional<R>> consumer) {
        AbstractJoinConditional<R> joinModel = new LambdaJoinConditional<>(joinClass);
        consumer.accept(joinModel);
        return addJoinTable(joinStyle, joinModel);
    }

    protected <R> LambdaJoinWrapper<T> addJoinTable(DbJoinStyle joinStyle, AbstractJoinConditional<R> joinConditional) {
        Asserts.unSupportOp(joinConditional.getJoinClass().equals(thisClass),
                String.format("暂不支持自关联或者一张表关联多次: [%s]", joinConditional.getJoinClass().getName()));

        Map<Class<?>, Long> joinClassMap = this.joinTableList.stream().filter(op -> JudgeUtil.isNotEmpty(op.getJoinTableAlias()))
                .collect(Collectors.groupingBy(AbstractJoinConditional::getJoinClass, Collectors.counting()));
        joinClassMap.forEach((joinClass, count) -> Asserts.unSupportOp(count > 1, String.format("不支持自关联或者一张表关联多次: [%s]", joinClass.getName())));

        this.aliasDefine(joinStyle, joinConditional);
        this.aliasList.add(joinConditional.getJoinTableAlias());
        this.joinTableList.add(joinConditional);
        return childrenThis;
    }


    private <R> void aliasDefine(DbJoinStyle joinStyle, AbstractJoinConditional<R> joinConditional) {
        if (aliasList.contains(joinConditional.getJoinTableAlias())) {
            ExThrowsUtil.toCustom("不允许存在相同的关联表别名: " + joinConditional.getJoinTableAlias());
        }

        String primaryAlias;
        Class<?> primaryClass = joinConditional.getPrimaryPropertyMap().getTargetClass();
        if (joinConditional.getPrimaryPropertyMap().getTargetClass().equals(primaryClass)) {
            primaryAlias = this.primaryTableAlias;
        }else {
            primaryAlias  = this.joinTableList.stream().filter(op -> op.getJoinClass().equals(primaryClass))
                    .findFirst()
                    .map(AbstractJoinConditional::getJoinTableAlias)
                    .orElse(null);
        }
        joinConditional.setJoinInfo(joinStyle, primaryAlias, aliasStrategy);
    }

    protected LambdaJoinWrapper<T> setAliasStrategy(AliasStrategy aliasStrategy) {
        this.aliasStrategy = aliasStrategy;
        return childrenThis;
    }

    public Class<T> getThisClass() {
        return thisClass;
    }

    public ColumnParseHandler<T> getThisColumnParseHandler() {
        return thisColumnParseHandler;
    }
}
