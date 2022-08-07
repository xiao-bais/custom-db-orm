package com.custom.action.condition;

import com.custom.action.util.DbUtil;
import com.custom.comm.Asserts;
import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtil;

import java.util.function.Consumer;

/**
 * @Author Xiao-Bai
 * @Date 2022/6/27 0027 12:01
 * @Desc 修改操作的sqlSet实体
 * T - 实体类型
 * Children - 子类类型
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
            Asserts.notNull(column, "column cannot be null");
            this.addSqlSetter(DbUtil.formatSetSql(column));
            Asserts.isIllegal(!CustomUtil.isBasicType(val),
                    String.format("Parameter types of type '%s' are not supported", val.getClass()));
            this.getSetParams().add(val);
        }
        return childrenClass;
    }

    protected Children addSetSql(boolean condition, SFunction<T, ?> column, Object val) {
        String originColumn = this.getColumnParseHandler().getColumn(column);
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
                    Asserts.isIllegal(!CustomUtil.isBasicType(param),
                            String.format("Parameter types of type '%s' are not supported", param.getClass()));
                    this.getSetParams().add(param);
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
            this.getSetParams().addAll(thisSqlSetter.getSetParams());
            this.addSqlSetter(thisSqlSetter.getSqlSetter());
        }
        return childrenClass;
    }
}
