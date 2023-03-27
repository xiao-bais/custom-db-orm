package com.home;

import com.custom.action.condition.Conditions;
import com.custom.action.condition.LambdaConditionWrapper;
import com.custom.action.core.JdbcDao;
import com.custom.action.core.JdbcOpDao;
import com.custom.action.core.syncquery.SyncProperty;
import com.custom.action.core.syncquery.SyncQueryWrapper;
import com.home.customtest.entity.Province;
import com.home.customtest.entity.Street;
import com.home.customtest.entity.Student;

import java.util.List;

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

        Student student = jdbcDao.selectOne(Conditions.syncQuery(Student.class)
                .primaryEx(x -> x.eq(Student::getNickName, "siyecao"))
                .injectProperty(Student::setModelList, x -> x.getModelList() == null, Conditions.lambdaQuery(Street.class).in(Street::getId, 5012, 5013, 5014, 5015))
                .injectProperty(Student::setProvince, x -> x.getProvince() == null,
                        t -> {
                            LambdaConditionWrapper<Province> wrapper = Conditions.lambdaQuery(Province.class);
                            return wrapper.in(t.getProId() != null, Province::getId, t.getProId());
                        })
        );

        System.out.println("students = " + 1);

    }









}
