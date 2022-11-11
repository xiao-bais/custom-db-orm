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


        Map<Integer, Integer> selectMap = jdbcDao.selectMap(Conditions.lambdaQuery(ChildStudent.class)
                .select(ChildStudent::getAge)
                .select(new SelectFunc<>(ChildStudent.class).count(ChildStudent::getAge, ChildStudent::getCountAge))
                .like(ChildStudent::getName, "78").or().gt(ChildStudent::getMoney, 5678)
                .addCutsomizeSql("and exists (select 1)")
                .or(false).like(ChildStudent::getName, "99")
                .or().like(ChildStudent::getName, "131")
//                .or(x -> x.like(ChildStudent::getName, "123"))
                        .groupBy(ChildStudent::getAge),
                Integer.class, Integer.class
        );


//        Map<Integer, String> selectMap = jdbcDao.selectMap(Conditions.lambdaQuery(ChildStudent.class)
//                        .groupBy(ChildStudent::getAge),
//                Integer.class, String.class
//        );

        for (Map.Entry<Integer, Integer> entry : selectMap.entrySet()) {
            System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
        }


    }




}
