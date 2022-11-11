package com.home;

import com.custom.action.condition.Conditions;
import com.custom.action.condition.SelectFunc;
import com.custom.action.interfaces.TableExecutor;
import com.custom.action.sqlparser.DefaultTableExecutor;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
import com.custom.comm.utils.CustomUtil;
import com.home.customtest.entity.ChildStudent;
import com.home.customtest.entity.Student;

import java.lang.reflect.Field;
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

        Map<Integer, String> selectMap = jdbcDao.selectMap(Conditions.lambdaQuery(ChildStudent.class)
                .select(ChildStudent::getAge)
                .select(x -> x.count(ChildStudent::getAge, ChildStudent::getIfNullAge))
                        .groupBy(ChildStudent::getAge),
                Integer.class, String.class
        );

        for (Map.Entry<Integer, String> entry : selectMap.entrySet()) {
            System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
        }


    }




}
