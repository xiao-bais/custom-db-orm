package com.home;

import com.custom.jdbc.back.BackResult;
import com.custom.jdbc.transaction.BackResultTransactionProxy;
import com.home.customtest.entity.Student;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:54
 * @Desc：
 **/
//@SuppressWarnings("all")
public class DoMain {

    public static void main(String[] args) throws Exception {

        BackResultTransactionProxy<Student> transactionProxy = new BackResultTransactionProxy<>();

        BackResult.Back<Student> proxyBack = transactionProxy.getBack();

        BackResult<Student> result = new BackResult<>();
        proxyBack.execCall(result);



//        JdbcTestBuilder jdbcTestBuilder = JdbcTestBuilder.builder();
//        JdbcDao jdbcDao = jdbcTestBuilder.getJdbcDao();
//        JdbcOpDao jdbcOpDao = jdbcTestBuilder.getJdbcOpDao();
//
//
//        jdbcOpDao.deleteBatchKeys(Aklis.class, Arrays.asList(1, 2, 3));
//
//        Aklis aklis = jdbcDao.selectByKey(Aklis.class, 123);

    }


}
