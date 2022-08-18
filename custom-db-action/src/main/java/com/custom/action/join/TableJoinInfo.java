package com.custom.action.join;

import com.custom.action.sqlparser.TableInfoCache;
import com.custom.comm.enums.DbJoinStyle;

/**
 * @Author Xiao-Bai
 * @Date 2022/8/19 1:34
 * @Desc 表关联相关信息
 */
public class TableJoinInfo<T> {

    /**
     * 关联表模板
     */
    private final static String JOIN_TABLE_TEMPLATE = "%s %s %s on %s";
    /**
     * 关联表
     */
    private final Class<T> joinClass;
    /**
     * 关联方式
     */
    private DbJoinStyle joinStyle = DbJoinStyle.LEFT;
    /**
     * 关联表名称
     */
    private final String tableName;
    /**
     * 关联表别名
     */
    private final String joinAlias;
    /**
     * 关联条件
     */
    private final String joinCondition;


    public Class<T> getJoinClass() {
        return joinClass;
    }

    public String getTableName() {
        return tableName;
    }

    public String getJoinAlias() {
        return joinAlias;
    }

    public String getJoinCondition() {
        return joinCondition;
    }

    public TableJoinInfo(Class<T> joinClass, DbJoinStyle joinStyle, String joinAlias, String joinCondition) {
        this(joinClass,  joinAlias, joinCondition);
        this.joinStyle = joinStyle;
    }

    public TableJoinInfo(Class<T> joinClass, String joinAlias, String joinCondition) {
        this.joinClass = joinClass;
        this.tableName = TableInfoCache.tableName(joinClass);
        this.joinAlias = joinAlias;
        this.joinCondition = joinCondition;
    }

    /**
     * 关联语句
     */
    public String joinTableInfo() {
        return String.format(JOIN_TABLE_TEMPLATE, this.joinStyle.getStyle(), this.tableName, this.joinAlias, this.joinCondition);
    }
}
