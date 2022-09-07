package com.home;

import com.custom.action.condition.Conditions;
import com.custom.action.condition.DefaultConditionWrapper;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.JdbcOpDao;
import com.custom.joiner.core.AbstractJoinConditional;
import com.custom.joiner.core.AbstractJoinWrapper;
import com.custom.joiner.core.LambdaJoinConditional;
import com.custom.joiner.core.LambdaJoinWrapper;
import com.home.customtest.entity.*;

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


        DefaultConditionWrapper<Student> conditionWrapper = Conditions.query(Student.class)
                .eq("sex", 1)
                .like("name", "张");
        long time1 = System.currentTimeMillis();
        List<Student> studentList = jdbcDao.selectList(conditionWrapper);
        long time2 = System.currentTimeMillis();
        conditionWrapper.gt("age", 20);
        List<Student> studentList1 = jdbcDao.selectList(conditionWrapper);
        long time3 = System.currentTimeMillis();
        conditionWrapper.or(op -> op.between("age", 20, 25).and(k -> k.eq("sex", 1).likeRight("name", "李"))
            .inSql("name", "select name from student stu where stu.age between ? and ?", 20, 24)
        );
        List<Student> studentList2 = jdbcDao.selectList(conditionWrapper);
        long time4 = System.currentTimeMillis();
        System.out.println("time2 = " + (time2-time1));
        System.out.println("time3 = " + (time3-time2));
        System.out.println("time4 = " + (time4-time3));


//        LambdaJoinWrapper<Student> joinWrapper = new LambdaJoinWrapper<>(Student.class);
//        LambdaJoinConditional<Province> joinConditional = new LambdaJoinConditional<>(Province.class).eq(Province::getId, Student::getProId).as("myPro");
//        joinWrapper.leftJoin(joinConditional);
//        joinWrapper.leftJoin(City.class, op -> op.eq(City::getId, Student::getCityId));
//        String sqlAction = joinConditional.formatJoinSqlAction();
//        System.out.println("sqlAction = " + sqlAction);
//        joinStyleWrapper.leftJoin(Province.class, join -> join.eq(Province::getId, Student::getProId).eq(Province::getName, Student::getName));




    }

}
