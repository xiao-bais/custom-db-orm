package com.custom.comm;

import com.alibaba.fastjson.JSON;
import com.custom.dbconfig.SymbolConst;
import com.custom.exceptions.CustomCheckException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
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
    public void sqlErrPrint() {
        if(sqlExecute) {
            handleExecuteSql();
            logger.error("QUERY-SQL ====>\n \n{}\n===================\n", sql);
        }else {
            logger.error(
                    "\nsql error\n===================\nSQL ====>\n {}\n===================\nparams = {}\n===================\n"
                    , sql, getFormatterParams());
        }

    }

    /**
     * PRINT-INFO-QUERY-SQL
     */
    public void sqlInfoQueryPrint() {
        if(sqlExecute) {
            handleExecuteSql();
            System.out.printf("QUERY-SQL ====>\n \n%s\n===================\n", sql);
        }else {
            System.out.printf("QUERY-SQL ====>\n \n%s\n===================\nparams = %s\n===================\n"
                    , sql, getFormatterParams());
        }

    }

    /**
     * PRINT-INFO-UPDATE-SQL
     */
    public void sqlInfoUpdatePrint() {
        if(sqlExecute) {
            handleExecuteSql();
            System.out.printf("UPDATE-SQL ====>\n \n%s\n===================\n", sql);
        }else {
            System.out.printf("UPDATE-SQL ====>\n \n%s\n===================\nparams = %s\n===================\n"
                    , sql, getFormatterParams());
        }
    }


    private  String sql;

    private final Object[] params;

    /**
     * true- 打印的是可执行的sql
     * false- 打印的是预编译的sql
     */
    private boolean sqlExecute = false;

    public SqlOutPrintBuilder(String sql, Object[] params, boolean sqlExecute) {
        this.sql = sql;
        this.params = params;
        this.sqlExecute = sqlExecute;
    }

    /**
     * 格式化参数打印
     */
    private String getFormatterParams() {
        StringJoiner sqlParams = new StringJoiner(SymbolConst.SEPARATOR_COMMA_2);
        for (Object param : params) {
            if(param == null) {
                sqlParams.add("null");
            }else {
                sqlParams.add(param + SymbolConst.BRACKETS_LEFT + param.getClass().getSimpleName() + SymbolConst.BRACKETS_RIGHT);
            }
        }
        return sqlParams.toString();
    }


    /**
     * 可执行的sql打印
     */
    private void handleExecuteSql() {
        int symbolSize = CustomUtil.countStr(sql, SymbolConst.QUEST);
        if(symbolSize != params.length) {
            sqlErrPrint();
            throw new CustomCheckException(String.format("参数数量与需要设置的参数数量不对等，需设置参数数量：%s, 实际参数数量：%s", symbolSize, params.length));
        }

        int index = 0;
        while (index < symbolSize) {
            Object param = params[index];
            if(param.getClass() == String.class) {
                param = String.format("'%s'", param);
            }
            sql = sql.replaceFirst("\\?", param.toString());
            index ++;
        }
    }

}
