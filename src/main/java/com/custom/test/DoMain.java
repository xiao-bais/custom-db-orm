package com.custom.test;

import com.custom.date.DateTimeUtils;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.handler.JdbcDao;

import java.util.Arrays;
import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2021/7/4
 * @Description
 */
public class DoMain {



    public static void main(String[] args) throws Exception {

        DbDataSource dbDataSource = new DbDataSource();
        dbDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/oneTest?characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8");
        dbDataSource.setDriver("com.mysql.cj.jdbc.Driver");
        dbDataSource.setUsername("root");
        dbDataSource.setPassword("123456");
        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
        dbCustomStrategy.setSqlOutPrinting(true);
        dbCustomStrategy.setUnderlineToCamel(true);
        dbDataSource.setDbCustomStrategy(dbCustomStrategy);

        JdbcDao jdbcDao = new JdbcDao(dbDataSource);

        String sql = "SELECT\n" +
                "  `stu_id`, `stu_name`, `stu_sex`, `stu_age`, `stu_birth`\n" +
                "FROM\n" +
                "  `onetest`.`student` ";

        int thisTime = DateTimeUtils.getThisTime();
        List<Person> personList = jdbcDao.selectListByKeys(Person.class, Arrays.asList(41,42,43,44,45,46,47,48,49,50,51,52));
        int nowTime = DateTimeUtils.getThisTime();

       int sub = nowTime - thisTime;

        System.out.println("sub = " + sub);

        for (Person person : personList) {
            System.out.println("person = " + person);
        }


    }
}
