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
import java.util.function.Consumer;

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
    private final List<AbstractJoinConditional<?, ?>> joinTableList;
    protected Children childrenThis = (Children) this;


    public AbstractJoinWrapper(Class<T> thisClass) {
        this.thisClass = thisClass;
        this.thisColumnParseHandler = new DefaultColumnParseHandler<>(thisClass);
        this.joinTableList = new ArrayList<>();
    }

    protected <B> Children addJoinTable(Class<B> bClass, Consumer<AbstractJoinConditional<B, T>> consumer) {
        AbstractJoinConditional<B, T> joinModel = new LambdaJoinConditional<>(bClass);
        this.registerAlias(joinModel);
        consumer.accept(joinModel);
        this.joinTableList.add(joinModel);
        return childrenThis;
    }

    protected  <A, B> Children addJoinTable(AbstractJoinConditional<A, B> joinConditional) {
        this.registerAlias(joinConditional);
        return childrenThis;
    }

    protected <B> Children addPrimaryInfo(AbstractJoinConditional<T, B> joinConditional) {
        joinConditional.setPrimaryTableInfo(thisClass, thisColumnParseHandler);
        return childrenThis;
    }



    protected <A, B> void registerAlias(AbstractJoinConditional<A, B> joinModel) {
        String primaryAlias = this.joinTableList.stream().filter(op -> JudgeUtil.isNotEmpty(op.getJoinTbaleAlias()) && op.getJoinTbaleAlias().equals(joinModel.getJoinTbaleAlias()))
                .findFirst()
                .map(AbstractJoinConditional::getJoinTbaleAlias).orElse(null);

        Asserts.notEmpty(primaryAlias, String.format("表 [%s] 未找到别名", joinModel.getPrimaryTableName()));

        joinModel.setPrimaryTableAlias(primaryAlias);
        String joinAlias = this.customAlias(joinModel.getJoinTableName(), joinModel.getJoinTbaleAlias());
        joinModel.setJoinTbaleAlias(joinAlias);

        this.joinTableList.stream().filter(op -> JudgeUtil.isNotEmpty(op.getJoinTbaleAlias()) && op.getJoinTbaleAlias().equals(joinModel.getJoinTbaleAlias()))
                .findFirst().ifPresent(join -> ExThrowsUtil.toIllegal(String.format("不允许存在相同的关联表别名：%s(%s) and %s(%s)",
                join.getPrimaryTableName(), join.getPrimaryTableAlias(),
                joinModel.getJoinTableName(), joinModel.getJoinTbaleAlias())
        ));
    }


    private String customAlias(String tableName, String joinAlias) {
        String newAlias;
        switch (this.aliasStrategy) {
            case INPUT:
                newAlias = joinAlias;
                Asserts.illegal(this.joinTableList.stream().anyMatch(op -> JudgeUtil.isNotEmpty(op.getJoinTbaleAlias())
                                && op.getJoinTbaleAlias().equals(joinAlias)),
                        String.format("存在已定义的表别名: [%s]", joinAlias)
                );
                break;
            case UNIQUE_ID:
                newAlias = JoinConstants.TABLE_ALIAS;
                while (this.joinTableList.stream().anyMatch(op -> JudgeUtil.isNotEmpty(op.getJoinTbaleAlias())
                        && op.getJoinTbaleAlias().equals(joinAlias))) {
                    newAlias = JoinConstants.TABLE_ALIAS;
                }
                break;
            default:
            case FIRST_APPEND:
                newAlias = CustomUtil.firstTableName(tableName);
                Asserts.illegal(this.joinTableList.stream().anyMatch(op -> JudgeUtil.isNotEmpty(op.getJoinTbaleAlias())
                                && op.getJoinTbaleAlias().equals(joinAlias)),
                        String.format("存在已定义的表别名: [%s]", joinAlias)
                );
        }
        return newAlias;
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
