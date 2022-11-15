package com.home;

import com.custom.action.condition.Conditions;
import com.custom.action.condition.SelectFunc;
import com.custom.action.interfaces.TableExecutor;
import com.custom.action.sqlparser.*;
import com.custom.comm.utils.CustomUtil;
import com.home.customtest.entity.ChildStudent;
import com.home.customtest.entity.Student;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

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


        List<ChildStudent> student = jdbcDao.selectList(Conditions.query(ChildStudent.class).select("age", "MIN(a.age) minAge")
                .select(x -> x.avg(ChildStudent::getIfNullAge, ChildStudent::getMaxAge))
            .groupBy("age")
        );
        System.out.println("student = " + student);


    }




}
