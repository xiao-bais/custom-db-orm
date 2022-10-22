package com.home;

import com.custom.action.condition.Conditions;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
import com.custom.jdbc.back.BackResult;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.HmErpOrderPO;
import com.home.customtest.entity.Student;

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

        HmErpOrderPO hmErpOrderPO1 = jdbcOpDao.selectByKey(HmErpOrderPO.class, 65521);
        HmErpOrderPO hmErpOrderPO2 = jdbcOpDao.selectByKey(HmErpOrderPO.class, 65522);
        HmErpOrderPO hmErpOrderPO3 = jdbcOpDao.selectByKey(HmErpOrderPO.class, 65523);

        jdbcDao.selectList(Conditions.lambdaQuery(Student.class).eq(Student::getNickName, "aaaad"));

        HmErpOrderPO hmErpOrderPO4 = jdbcOpDao.selectByKey(HmErpOrderPO.class, 65524);
        HmErpOrderPO hmErpOrderPO5 = jdbcOpDao.selectByKey(HmErpOrderPO.class, 65525);
        HmErpOrderPO hmErpOrderPO6 = jdbcOpDao.selectByKey(HmErpOrderPO.class, 65526);

        System.out.printf("该订单【%s】, 套餐售价【%d】元", hmErpOrderPO1.getOrderNum(), hmErpOrderPO1.getActivAmount());


    }


}
