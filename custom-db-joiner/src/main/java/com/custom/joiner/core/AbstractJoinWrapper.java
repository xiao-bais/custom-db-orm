package com.custom.joiner.core;

import com.custom.action.condition.DefaultColumnParseHandler;
import com.custom.action.interfaces.ColumnParseHandler;
import com.custom.comm.Asserts;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtil;
import com.custom.comm.exceptions.ExThrowsUtil;
import com.custom.joiner.condition.AbstractJoinConditional;
import com.custom.joiner.condition.LambdaJoinConditional;
import com.custom.joiner.enums.AliasStrategy;
import com.custom.joiner.util.JoinConstants;

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
public abstract class AbstractJoinWrapper<T, Children> {

    private final Class<T> thisClass;
    private AliasStrategy aliasStrategy;
    private final ColumnParseHandler<T> thisColumnParseHandler;
    private final List<AbstractJoinConditional<?>> joinTableList;
    protected Children childrenThis = (Children) this;


    public AbstractJoinWrapper(Class<T> thisClass) {
        this.thisClass = thisClass;
        this.thisColumnParseHandler = new DefaultColumnParseHandler<>(thisClass);
        this.joinTableList = new ArrayList<>();
    }

    protected <R> Children addJoinTable(Class<R> joinClass, Consumer<AbstractJoinConditional<R>> consumer) {
        AbstractJoinConditional<R> joinModel = new LambdaJoinConditional<>(joinClass);
        consumer.accept(joinModel);
        return addJoinTable(joinModel);
    }

    protected <R> Children addJoinTable(AbstractJoinConditional<R> joinConditional) {
        this.joinTableList.add(joinConditional);
        return childrenThis;
    }



    protected void registerAlias() {
        Map<Class<?>, Long> joinClassMap = this.joinTableList.stream().filter(op -> JudgeUtil.isNotEmpty(op.getJoinTableAlias()))
                .collect(Collectors.groupingBy(AbstractJoinConditional::getJoinClass, Collectors.counting()));
        joinClassMap.forEach((joinClass, count) -> Asserts.unSupportOp(count > 1, String.format("不支持自关联或者一张表关联多次: [%s]", joinClass)));

        if (this.aliasStrategy == AliasStrategy.INPUT) {
            Map<String, Long> joinAliasMap = this.joinTableList.stream()
                    .collect(Collectors.groupingBy(AbstractJoinConditional::getJoinTableAlias, Collectors.counting()));
            joinAliasMap.forEach((joinAlias, count) ->
                    Asserts.illegal(count > 1,
                            "不允许存在相同的关联表别名: " + joinAlias));
        }
        this.aliasDefine();
    }


    private void aliasDefine() {
        List<String> aliasList = new ArrayList<>(this.joinTableList.size());
        switch (this.aliasStrategy) {
            case INPUT:
                this.joinTableList.stream().filter(op -> JudgeUtil.isNotEmpty(op.getJoinTableAlias())).findFirst().ifPresent(op -> {
                    ExThrowsUtil.toCustom("存在未定义的表别名: " + op.getJoinClass().getName());
                });
                break;
            default:
            case UNIQUE_ID:

                for (AbstractJoinConditional<?> joinModel : this.joinTableList) {
                    String newAlias = "";
                    do {
                        newAlias = JoinConstants.TABLE_ALIAS;
                    } while (aliasList.contains(newAlias));
                    joinModel.alias(newAlias);
                    aliasList.add(newAlias);
                }

                break;
            case FIRST_APPEND:
                for (AbstractJoinConditional<?> joinModel : this.joinTableList) {
                    String newAlias = CustomUtil.firstTableName(joinModel.getJoinPropertyMap().getTableName());
                    Asserts.illegal(aliasList.contains(newAlias),
                            String.format("存在已定义的表别名: [%s]", newAlias));
                }

        }
    }

    protected Children setAliasStrategy(AliasStrategy aliasStrategy) {
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
