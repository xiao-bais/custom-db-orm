package com.home;

import com.custom.action.condition.Conditions;
import com.custom.action.core.JdbcDao;
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

        jdbcDao.execTrans(() -> {
            // 逻辑写在里面即可
            Employee employee = jdbcDao.selectByKey(Employee.class, 10);
            employee.setEmpName("zhangsan");
            jdbcDao.updateByKey(employee);
            int a = 1 / 0;
            employee.setEmpName("lisi");
            jdbcDao.updateByKey(employee);
        });



    }





    private static void testInjectProperty(JdbcDao jdbcDao) {

        Student student = jdbcDao.selectOne(Conditions.syncQuery(Student.class)
                // student对象的查询(即主对象)
                .primaryEx(x -> x.eq(Student::getNickName, "siyecao"))
                // student对象中某个非持久化属性的查询(即一对一、一对多)
                // t 即是查询后的student对象，作为预判断提前使用
                .property(Student::setModelList, t -> t.getModelList() == null,
                        Conditions.lambdaQuery(Street.class).in(Street::getId, 5012, 5013, 5014, 5015))
                .property(Student::setProvince, t -> t.getProvince() == null,
                        t -> Conditions.lambdaQuery(Province.class).in(t.getProId() != null, Province::getId, t.getProId()))
        );

        Province province = student.getProvince();
        List<City> cityList = jdbcDao.selectList(Conditions.syncQuery(City.class)
                .primaryEx(x -> x.eq(City::getProvinceId, province.getId()))
                .property(City::setLocationList, t -> Conditions.lambdaQuery(Location.class).in(Location::getCityId, t.getId()))
        );
        province.setCityList(cityList);

        System.out.println("1 = " + 1);
    }


}
