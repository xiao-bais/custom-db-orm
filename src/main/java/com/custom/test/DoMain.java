package com.custom.test;

import com.custom.date.DateTimeUtils;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.jdbc.JdbcDao;
import com.custom.page.AutoPageUtil;
import com.custom.page.DbPageRows;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2021/7/4
 * @Description
 */
public class DoMain {



    public static void main(String[] args) throws Exception {

        DbDataSource dbDataSource = new DbDataSource();
        dbDataSource.setUrl("jdbc:mysql://127.0.0.1/hos?characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8");
        dbDataSource.setDriver("com.mysql.cj.jdbc.Driver");
        dbDataSource.setUsername("root");
        dbDataSource.setPassword("123456");
        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
        dbCustomStrategy.setPrintSqlFlag(true);
        dbDataSource.setDbCustomStrategy(dbCustomStrategy);

        JdbcDao jdbcDao = new JdbcDao(dbDataSource);

//        jdbcDao.createTables(Person.class);

//        jdbcDao.insert(new Person("李海霞", 25, false, DateTimeUtils.getDateByFormatDate("1995-09-26"), "她很可怜"));

//        Person person = new Person("李海霞", 26, false, DateTimeUtils.getDateByFormatDate("1995-09-26"), "她很可怜");
//        person.setId(3);
        List<Person> personList = jdbcDao.selectList(Person.class, "");
        AutoPageUtil<Person> personDbPageRows = new AutoPageUtil<>(1, 6, personList);
        System.out.println("person = " + personDbPageRows);


    }
}
