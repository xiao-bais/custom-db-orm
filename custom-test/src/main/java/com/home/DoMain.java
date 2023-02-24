package com.home;

import com.custom.action.core.JdbcDao;
import com.custom.action.core.JdbcOpDao;
import com.custom.action.service.DbServiceHelper;
import com.custom.action.service.DbServiceImplHelper;
import com.home.customtest.entity.Student;
import com.home.customtest.entity.WorkEmp;

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

//        JdbcTestBuilder jdbcTestBuilder = JdbcTestBuilder.builder();
//        JdbcDao jdbcDao = jdbcTestBuilder.getJdbcDao();
//        JdbcOpDao jdbcOpDao = jdbcTestBuilder.getJdbcOpDao();
//        MyService helper = new MyServiceImpl();

//        List<Student> studentList = helper.getByKeys(Arrays.asList(11, 12, 13));
//        List<Map<String, Object>> mapList = helper.where(x -> x.eq("age", 25).orderByAsc("money")).getMaps();



//        System.out.println("studentList.size() = " + studentList.size());

        WorkEmp workEmp = new WorkEmp();
        workEmp.setMyInf(System::currentTimeMillis);

        Object o1 = workEmp.getMyInf().get();
        System.out.println("o1 = " + o1);
        Thread.sleep(2000);
        Object o2 = workEmp.getMyInf().get();
        System.out.println("o2 = " + o2);

    }

    interface DoInf {
        Object get();
    }








}
