package com.home;

import com.custom.action.core.JdbcDao;
import com.custom.action.core.JdbcOpDao;
import com.custom.action.service.DbServiceHelper;
import com.custom.action.service.DbServiceImplHelper;
import com.home.customtest.entity.Student;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author  Xiao-Bai
 * @since  2021/11/29 12:54
 * @Desc：
 **/
//@SuppressWarnings("all")
public class DoMain {

    public static void main(String[] args) throws Exception {

        JdbcTestBuilder jdbcTestBuilder = JdbcTestBuilder.builder();
        JdbcDao jdbcDao = jdbcTestBuilder.getJdbcDao();
        JdbcOpDao jdbcOpDao = jdbcTestBuilder.getJdbcOpDao();
        MyService helper = new MyServiceImpl();

        List<Student> studentList = helper.getByKeys(Arrays.asList(11, 12, 13));
        List<Map<String, Object>> mapList = helper.where(x -> x.eq("age", 25).orderByAsc("money")).getMaps();



        System.out.println("studentList.size() = " + studentList.size());

    }








}
