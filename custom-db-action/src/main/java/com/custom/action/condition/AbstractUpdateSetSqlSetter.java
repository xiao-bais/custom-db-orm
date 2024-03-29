package com.custom.action.condition;

import com.custom.action.util.DbUtil;
import com.custom.comm.utils.AssertUtil;
import com.custom.comm.utils.CustomUtil;
import com.custom.comm.utils.JudgeUtil;
import com.custom.comm.utils.lambda.SFunction;

import java.util.function.Consumer;

/**
 * 修改操作的sqlSet实体
 * @param <T> 实体类型
 * @param <Children> 子类类型
 * @author   Xiao-Bai
 * @since  2022/6/27 0027 12:01
 */
public abstract class AbstractUpdateSetSqlSetter<T, Children> extends UpdateSetWrapper<T> {

    /**
     * 子类实例
     */
    protected abstract Children getInstance();
    @SuppressWarnings("unchecked")
    protected final Children childrenClass = (Children) this;


    protected Children addSetSql(boolean condition, String column, Object val) {
        if (condition) {
            AssertUtil.notNull(column, "column cannot be null");
            this.addSqlSetter(DbUtil.formatSqlCondition(column));
            AssertUtil.illegal(!CustomUtil.isBasicType(val),
                    String.format("Parameter types of type '%s' are not supported", val.getClass()));
            this.addParams(val);
        }
        return childrenClass;
    }

    protected Children addSetSql(boolean condition, SFunction<T, ?> column, Object val) {
        String originColumn = this.getColumnParseHandler().parseToColumn(column);
        return this.addSetSql(condition, originColumn, val);
    }


    protected AbstractUpdateSetSqlSetter(Class<T> entityClass) {
        super(entityClass);
    }

    /**
     * 添加自定义setSql
     */
    protected Children addSetSqlString(boolean condition, String setSql, Object... params) {
        if (condition) {
            this.addSqlSetter(setSql);
            if (JudgeUtil.isNotEmpty(params)) {
                for (Object param : params) {
                    AssertUtil.illegal(!CustomUtil.isBasicType(param),
                            String.format("Parameter types of type '%s' are not supported", param.getClass()));
                    this.addParams(param);
                }
            }
        }
        return childrenClass;
    }

    /**
     * 消费型sql set
     */
    @SuppressWarnings("unchecked")
    protected Children consumerSet(boolean condition, Consumer<Children> consumer) {
        if (condition) {
            Children instance = getInstance();
            consumer.accept(instance);
            AbstractUpdateSetSqlSetter<T, Children> thisSqlSetter = (AbstractUpdateSetSqlSetter<T, Children>) instance;
            this.addParams(thisSqlSetter.getSetParams());
            this.addSqlSetter(thisSqlSetter.getSqlSetter());
        }
        return childrenClass;
    }
}
