package com.home;

import com.custom.action.core.*;
import com.custom.comm.utils.CustomUtil;
import com.custom.jdbc.back.BackResult;
import com.home.customtest.entity.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        Student student = jdbcDao.selectByKey(ChildStudent.class, 13);
        student.save();



    }






}
