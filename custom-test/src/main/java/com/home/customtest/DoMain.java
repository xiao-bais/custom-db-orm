package com.home.customtest;

import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.handler.proxy.SqlReaderExecuteProxy;
import com.home.customtest.dao.CustomDao;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:54
 * @Desc：
 **/
public class DoMain {


    public static void main(String[] args) {

        DbDataSource dbDataSource = new DbDataSource();
        dbDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/hos?characterEncoding=utf-8&allowMultiQueries=true&autoreconnect=true&serverTimezone=UTC");
        dbDataSource.setUsername("root");
        dbDataSource.setPassword("123456");

        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
        dbCustomStrategy.setMapperScanEnable(true);
        dbCustomStrategy.setPackageScans(new String[]{"com.custom.customtest.dao"});

        CustomDao customDao = new SqlReaderExecuteProxy(dbDataSource, dbCustomStrategy).createProxy(CustomDao.class);
        String s = customDao.selectOneByCond(1, 25);
        String code = System.getenv("ENV_CODE");
        System.out.println("code = " + code);
        System.out.println("s = " + s);


    }
}
