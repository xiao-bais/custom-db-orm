package com.custom.comm;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author Xiao-Bai
 * @date 2021/11/5 22:46
 * @desc: sql打印输出处理
 */
public class SqlOutPrintBuilder implements Serializable {

    private Logger logger = LoggerFactory.getLogger(SqlOutPrintBuilder.class);


    /**
     * PRINT-ERROR-SQL
     */
    public void sqlErrPrint() {
        logger.info(
                "\nsql error\n===================\nSQL ====>\n {}\n===================\nparams = {}\n===================\n"
                , sql, JSON.toJSONString(params));
    }

    /**
     * PRINT-INFO-QUERY-SQL
     */
    public void sqlInfoQueryPrint() {
        System.out.printf("QUERY-SQL ====>\n \n%s\n===================\nparams = %s\n===================\n"
                , sql, JSON.toJSONString(params));
    }

    /**
     * PRINT-INFO-UPDATE-SQL
     */
    public void sqlInfoUpdatePrint() {
        System.out.printf("UPDATE-SQL ====>\n %s\n===================\nparams = %s\n===================\n"
                , sql, JSON.toJSONString(params));
    }


    private String sql;

    private Object[] params;

    public SqlOutPrintBuilder(String sql, Object[] params) {
        this.sql = sql;
        this.params = params;
    }
}
