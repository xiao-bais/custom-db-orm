package com.custom.taskmanager.config;

import com.custom.jdbc.executor.CustomSqlQueryAfter;

/**
 * @author Xiao-Bai
 * @since 2023/2/22 18:21
 */
public class MySqlQueryAfter implements CustomSqlQueryAfter {

    @Override
    public <T> void handle(Class<T> target, Object result) throws Exception {
//        System.out.println("查询之后的处理...");
//        System.out.println("result = " + result);
    }
}
