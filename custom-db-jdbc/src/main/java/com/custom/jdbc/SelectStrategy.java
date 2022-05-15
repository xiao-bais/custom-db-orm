package com.custom.jdbc;

/**
 * @author Xiao-Bai
 * @date 2022/5/15 19:42
 * @desc:查询策略
 */
public class SelectStrategy {

    /**
     * 是否输出打印sql
     */
    private boolean sqlOutPrint;

    /**
     * 在查询时，若是字符类型，null是否转为空字符
     */
    private boolean nullToEmpty;


    public boolean isSqlOutPrint() {
        return sqlOutPrint;
    }

    public void setSqlOutPrint(boolean sqlOutPrint) {
        this.sqlOutPrint = sqlOutPrint;
    }

    public boolean isNullToEmpty() {
        return nullToEmpty;
    }

    public void setNullToEmpty(boolean nullToEmpty) {
        this.nullToEmpty = nullToEmpty;
    }
}
