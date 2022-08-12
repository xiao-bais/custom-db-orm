package com.home;

import com.custom.action.condition.*;
import com.custom.action.interfaces.BaseEntityDao;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.sqlparser.TableSqlBuilder;
import com.custom.comm.BasicDao;
import com.custom.comm.CustomUtil;
import com.home.customtest.dao.StudentDao;
import com.home.customtest.entity.ChildStudent;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.Student;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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

        StudentDao studentDao = jdbcTestBuilder.getCustomClassDao(StudentDao.class);

        Student student = studentDao.selectByKey(13);



    }

}
