package com.custom.comm;

import com.alibaba.fastjson.JSON;
import com.custom.dbconfig.SymbolConst;
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
        logger.error(
                "\nsql error\n===================\nSQL ====>\n {}\n===================\nparams = {}\n===================\n"
                , sql, getFormatterParams());
    }

    /**
     * PRINT-INFO-QUERY-SQL
     */
    public void sqlInfoQueryPrint() {
        System.out.printf("QUERY-SQL ====>\n \n%s\n===================\nparams = %s\n===================\n"
                , sql, getFormatterParams());
    }

    /**
     * PRINT-INFO-UPDATE-SQL
     */
    public void sqlInfoUpdatePrint() {
        System.out.printf("UPDATE-SQL ====>\n %s\n===================\nparams = %s\n===================\n"
                , sql, getFormatterParams());
    }


    private final String sql;

    private final Object[] params;

    public SqlOutPrintBuilder(String sql, Object[] params) {
        this.sql = sql;
        this.params = params;
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
}
