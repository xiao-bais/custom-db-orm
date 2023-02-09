package com.custom.action.condition;

import com.custom.action.interfaces.ColumnParseHandler;
import com.custom.comm.utils.lambda.SFunction;

/**
 * lambda格式的exists条件包装对象
 * @author   Xiao-Bai
 * @since  2023/1/12 0012 13:53
 */
public class LambdaExistsWrapper<P, E> implements ExistsWrapper<P, E> {

    private SFunction<P, ?> proColumn;
    private SFunction<E, ?> existColumn;
    private final ColumnParseHandler<E> columnParseHandler;
    private final LambdaConditionWrapper<E> existWrapper;


    @Override
    public LambdaConditionWrapper<E> apply(SFunction<P, ?> proColumn, SFunction<E, ?> existColumn) {
        this.proColumn = proColumn;
        this.existColumn = existColumn;
        return existWrapper;
    }


    public LambdaConditionWrapper<E> getWrapper() {
        return existWrapper;
    }

    public LambdaExistsWrapper(Class<E> existClass) {
        this.existWrapper = new LambdaConditionWrapper<>(existClass, false);
        this.columnParseHandler = new DefaultColumnParseHandler<>(existClass, existWrapper.getTableSupport());
    }

    public SFunction<P, ?> getProColumn() {
        return proColumn;
    }

    public String getExistColumn() {
        return columnParseHandler.parseToNormalColumn(existColumn);
    }
}
