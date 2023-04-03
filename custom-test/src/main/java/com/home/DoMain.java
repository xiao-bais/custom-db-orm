package com.home;

import com.custom.action.condition.Conditions;
import com.custom.action.condition.LambdaConditionWrapper;
import com.custom.action.core.JdbcDao;
import com.custom.action.core.JdbcOpDao;
import com.custom.action.core.TableInfoCache;
import com.custom.action.core.TableParseModel;
import com.custom.action.core.syncquery.SyncProperty;
import com.custom.action.core.syncquery.SyncQueryWrapper;
import com.home.customtest.entity.*;

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
//        JdbcOpDao jdbcOpDao = jdbcTestBuilder.getJdbcOpDao();
//        MyService helper = new MyServiceImpl();

//        jdbcDao.createTables(Student.class);

        Employee employee = jdbcDao.selectByKey(Employee.class, 10);
        int selective = jdbcDao.updateByCondition(employee, "and a.id = ?", employee.getId());


        System.out.println("students = " + 1);

    }





    private static void testInjectProperty(JdbcDao jdbcDao) {
        Student student = jdbcDao.selectOne(Conditions.syncQuery(Student.class)
                .primaryEx(x -> x.eq(Student::getNickName, "siyecao"))
                .injectProperty(Student::setModelList, x -> x.getModelList() == null, Conditions.lambdaQuery(Street.class).in(Street::getId, 5012, 5013, 5014, 5015))
                .injectProperty(Student::setProvince, x -> x.getProvince() == null,
                        t -> Conditions.lambdaQuery(Province.class).in(t.getProId() != null, Province::getId, t.getProId()))
        );

        Province province = student.getProvince();
        List<City> cityList = jdbcDao.selectList(Conditions.syncQuery(City.class)
                .primaryEx(x -> x.eq(City::getProvinceId, province.getId()))
                .injectProperty(City::setLocationList, t -> Conditions.lambdaQuery(Location.class).in(Location::getCityId, t.getId()))
        );
        province.setCityList(cityList);
        System.out.println("1 = " + 1);
    }


}
