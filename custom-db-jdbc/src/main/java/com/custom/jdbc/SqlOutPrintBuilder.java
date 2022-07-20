package com.custom.jdbc;

import com.custom.comm.CustomUtil;
import com.custom.comm.SymbolConstant;
import com.custom.comm.exceptions.ExThrowsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * @author Xiao-Bai
 * @date 2021/11/5 22:46
 * @desc: sql打印输出处理
 */
public class SqlOutPrintBuilder implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(SqlOutPrintBuilder.class);


    /**
     * PRINT-ERROR-SQL
     */
    protected void sqlErrPrint() {
        if(sqlExecute) {
            handleExecuteSql();
            logger.error("Sql Error ====>\n \n{}\n===================\n", sql);
        }else {
            logger.error(
                    "\nSql Error\n===================\nERROR SQL ====>\n {}\n===================\nParameters = {}\n===================\n"
                    , sql, formatterParams());
        }

    }

    /**
     * PRINT-INFO-QUERY-SQL
     */
    protected void sqlInfoQueryPrint() {
        if(sqlExecute) {
            handleExecuteSql();
            logger.info("QUERY-SQL ====>\n \n{}\n===================\n", sql);
        }else {
            logger.info("QUERY-SQL ====>\n \n{}\n===================\nParameters = {}\n===================\n"
                    , sql, formatterParams());
        }
    }

    /**
     * PRINT-INFO-UPDATE-SQL
     */
    protected void sqlInfoUpdatePrint() {
        if(sqlExecute) {
            handleExecuteSql();
            logger.info("UPDATE-SQL ====>\n \n{}\n===================\n", sql);
        }else {
            logger.info("UPDATE-SQL ====>\n \n{}\n===================\nParameters = {}\n===================\n"
                    , sql, formatterParams());
        }
    }


    private  String sql;

    private final Object[] params;

    /**
     * true- 打印的是可执行的sql
     * false- 打印的是预编译的sql
     */
    private final boolean sqlExecute;

    private SqlOutPrintBuilder(String sql, Object[] params, boolean sqlExecute) {
        this.sql = sql;
        this.params = params;
        this.sqlExecute = sqlExecute;
    }


    /**
     * 构建sql打印
     */
    protected static SqlOutPrintBuilder build(String sql, Object[] params, boolean sqlExecute) {
        return new SqlOutPrintBuilder(sql, params, sqlExecute);
    }


    /**
     * 格式化参数打印
     */
    private String formatterParams() {
        StringJoiner sqlParams = new StringJoiner(SymbolConstant.SEPARATOR_COMMA_2);
        for (Object param : params) {
            StringBuilder paramVal = new StringBuilder();
            if (param == null) {
                sqlParams.add(SymbolConstant.NULL);
            }else if (SymbolConstant.EMPTY.equals(param)) {
                paramVal.append(SymbolConstant.EMPTY_SQL_STR).append(SymbolConstant.BRACKETS_LEFT)
                        .append(param.getClass().getSimpleName()).append(SymbolConstant.BRACKETS_RIGHT);
            }else {
                paramVal.append(param).append(SymbolConstant.BRACKETS_LEFT)
                        .append(param.getClass().getSimpleName()).append(SymbolConstant.BRACKETS_RIGHT);
            }
            sqlParams.add(paramVal);
        }
        return sqlParams.toString();
    }


    /**
     * 可执行的sql打印
     */
    private void handleExecuteSql() {
        int symbolSize = CustomUtil.countStr(sql, SymbolConstant.QUEST);
        if(symbolSize != params.length) {
            logger.error(
                    "\nSql Error\n===================\nSQL ====>\n {}\n===================\nParameters = {}\n===================\n"
                    , sql, formatterParams());
            ExThrowsUtil.toCustom(String.format("参数数量与需要设置的参数数量不对等，需设置参数数量：%s, 实际参数数量：%s", symbolSize, params.length));
        }
        sql = CustomUtil.handleExecuteSql(sql, params);
    }

}
