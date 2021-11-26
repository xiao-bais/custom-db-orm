package com.custom.onetest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/23 16:38
 * @Desc：
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomApplicationTests {


    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void test1(){

//        DbDataSource dbDataSource = new DbDataSource();
//        dbDataSource.setUrl("jdbc:mysql://127.0.0.1/smbms?characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8");
//        dbDataSource.setUrl("jdbc:mysql://127.0.0.1/hos?characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8");
//        dbDataSource.setDriver("com.mysql.cj.jdbc.Driver");
//        dbDataSource.setUsername("root");
//        dbDataSource.setPassword("123456");
//        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
//        dbCustomStrategy.setPackageScans(new String[]{"com.custom.test"});
//        dbCustomStrategy.setMapperScanEnable(true);

//        //是否打印执行的sql 默认false
//        dbCustomStrategy.setSqlOutPrinting(true);
//        dbCustomStrategy.setSqlOutUpdate(true);
//        dbCustomStrategy.setDbFieldDeleteLogic("state");
//        dbCustomStrategy.setDeleteLogicValue("1");
//        dbCustomStrategy.setNotDeleteLogicValue("0");
//        dbDataSource.setDbCustomStrategy(dbCustomStrategy);
//        dbCustomStrategy.setUnderlineToCamel(true);
//        JdbcDao jdbcDao = new JdbcDao(dbDataSource);


//        JdbcTest jdbcTest  = new SqlReaderExecuteProxy(dbDataSource).createProxy(JdbcTest.class);

    }


}
