package com.home;

import com.custom.action.condition.Conditions;
import com.custom.action.core.JdbcDao;
import com.custom.action.core.JdbcOpDao;
import com.custom.comm.enums.DatabaseDialect;
import com.custom.comm.page.DbPageRows;
import com.custom.jdbc.dbAdapetr.AbstractDbAdapter;
import com.custom.jdbc.dbAdapetr.OracleAdapter;
import com.custom.jdbc.dbAdapetr.PostgresqlAdapter;
import com.custom.jdbc.interfaces.DatabaseAdapter;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.Student;

import java.math.BigDecimal;
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

        JdbcTestBuilder jdbcTestBuilder = JdbcTestBuilder.builder();
        JdbcDao jdbcDao = jdbcTestBuilder.getJdbcDao();
        JdbcOpDao jdbcOpDao = jdbcTestBuilder.getJdbcOpDao();

        MyService helper = new MyServiceImpl();

//        Student student = jdbcDao.selectByKey(Student.class, 123);

        String sql = "SELECT a.id id, a.sex sex, a.phone phone, a.name name, a.nick_code nickName, a.password password, a.age age, a.money money, a.address address, a.birthday birth, a.state state, a.pro_id proId, a.city_id cityId, a.area_id areaId, pv.name provinceName\n" +
                " FROM student a\n" +
                " left join province pv on pv.id = a.pro_id\n" +
                "WHERE a.state = 0 AND (a.id = 123)";
        String url = "jdbc:postgresql://localhost:127.0.0.1:5432/onetest1?";
//        int indexOf = url.indexOf(63);
//        System.out.println("indexOf = " + indexOf);
        PostgresqlAdapter adapter = new PostgresqlAdapter(jdbcOpDao.getSqlExecutor().getSqlSessionFactory());
        String databaseName = adapter.databaseName(url);
        System.out.println("databaseName = " + databaseName);

        System.out.println("students = " + 1);

    }









}
