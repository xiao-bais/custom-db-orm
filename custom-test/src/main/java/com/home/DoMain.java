package com.home;

import com.custom.action.interfaces.TableExecutor;
import com.custom.action.sqlparser.DefaultTableExecutor;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
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

        TableExecutor<Student, Integer> tableExecutor = new DefaultTableExecutor<>(
                jdbcTestBuilder.getDbDataSource(),
                jdbcTestBuilder.getDbCustomStrategy(),
                Student.class);

    }


}
