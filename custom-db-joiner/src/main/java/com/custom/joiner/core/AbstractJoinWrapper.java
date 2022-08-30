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
public abstract class AbstractJoinWrapper<T> {

    private final Class<T> thisClass;
    private AliasStrategy aliasStrategy;
    private final ColumnParseHandler<T> thisColumnParseHandler;
    private final List<AbstractJoinConditional<?, ?>> joinTableList;


    public AbstractJoinWrapper(Class<T> thisClass) {
        this.thisClass = thisClass;
        this.thisColumnParseHandler = new DefaultColumnParseHandler<>(thisClass);
        this.joinTableList = new ArrayList<>();
    }

    public <B> void addJoinTable(Class<B> bClass, Consumer<AbstractJoinConditional<T, B>> consumer) {
        this.addJoinTable(thisClass, bClass, consumer);
    }

    public <A, B> void addJoinTable(Class<A> aClass, Class<B> bClass, Consumer<AbstractJoinConditional<A, B>> consumer) {
        AbstractJoinConditional<A, B> joinModel = new LambdaJoinConditional<>(aClass, bClass);
        String primaryAlias = this.joinTableList.stream().filter(op -> JudgeUtil.isNotEmpty(op.getJoinTbaleAlias()) && op.getJoinTbaleAlias().equals(joinModel.getJoinTbaleAlias()))
                .findFirst()
                .map(AbstractJoinConditional::getJoinTbaleAlias).orElse(null);

        Asserts.notEmpty(primaryAlias, String.format("表 [%s] 未定义别名", joinModel.getPrimaryTableName()));

        joinModel.setPrimaryTableAlias(primaryAlias);
        String joinAlias = this.customAlias(joinModel.getJoinTableName(), joinModel.getJoinTbaleAlias());
        joinModel.setJoinTbaleAlias(joinAlias);

        this.joinTableList.stream().filter(op -> JudgeUtil.isNotEmpty(op.getJoinTbaleAlias()) && op.getJoinTbaleAlias().equals(joinModel.getJoinTbaleAlias()))
                .findFirst().ifPresent(join -> ExThrowsUtil.toIllegal(String.format("不允许存在相同的关联表别名：%s(%s) and %s(%s)",
                join.getPrimaryTableName(), join.getPrimaryTableAlias(),
                joinModel.getJoinTableName(), joinModel.getJoinTbaleAlias())
        ));

        consumer.accept(joinModel);
        this.joinTableList.add(joinModel);
    }



    private String customAlias(String tableName, String joinAlias) {
        String newAlias = "";
        switch (this.aliasStrategy) {
            case INPUT:
                newAlias = joinAlias;
                break;
            case UNIQUE_ID:
                newAlias = JoinConstants.TABLE_ALIAS;
                break;
            case FIRST_APPEND:
                newAlias = CustomUtil.firstTableName(tableName);
        }
        return newAlias;
    }

    protected void setAliasStrategy(AliasStrategy aliasStrategy) {
        this.aliasStrategy = aliasStrategy;
    }
}
