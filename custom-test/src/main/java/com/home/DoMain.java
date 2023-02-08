package com.home;

import com.custom.action.core.JdbcDao;
import com.custom.action.core.JdbcOpDao;
import com.custom.action.service.DbServiceHelper;
import com.home.customtest.entity.Student;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:54
 * @Desc：
 **/
//@SuppressWarnings("all")
public class DoMain {

    public static void main(String[] args) throws Exception {

        JdbcTestBuilder jdbcTestBuilder = JdbcTestBuilder.builder();
        JdbcDao jdbcDao = jdbcTestBuilder.getJdbcDao();
        JdbcOpDao jdbcOpDao = jdbcTestBuilder.getJdbcOpDao();
        DbServiceHelper<Student> helper = new MyServiceImpl();







    }








}
