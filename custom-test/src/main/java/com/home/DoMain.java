package com.home;

import com.custom.action.sqlparser.*;
import com.home.customtest.entity.ChildStudent;

import java.lang.reflect.Constructor;
import java.util.List;

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


        List<ChildStudent> student = jdbcDao.selectList(ChildStudent.class, "and a.name = ? or a.nick_code = ?", "宋希于", "jiangyun");
        List<ChildStudent> student2 = jdbcDao.selectList(ChildStudent.class, "and a.name = ? or a.nick_code = ?", "宋希于", "jiangyun");
        System.out.println("student = " + student);


    }




}
