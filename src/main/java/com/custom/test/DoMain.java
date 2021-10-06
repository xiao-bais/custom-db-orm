package com.custom.test;

import com.custom.dbconfig.DbDataSource;
import com.custom.jdbc.JdbcDao;
import com.custom.jdbc.JdbcUtils;
import com.custom.utils.CommUtils;
import com.custom.utils.DbPageRows;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

        JdbcDao jdbcDao = new JdbcDao(dbDataSource);

        jdbcDao.createTables(Student.class);

        Student stu = new Student();
        stu.setAge(27);
        stu.setName("李民");
        stu.setBirthday(new SimpleDateFormat("yyyyMMdd").parse("20020918"));
        stu.setMarry(false);

        jdbcDao.insert(stu);




    }
}
