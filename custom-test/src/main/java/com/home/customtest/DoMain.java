package com.home.customtest;

import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.sqlparser.CustomDao;
import com.custom.sqlparser.TableInfoCache;
import com.custom.wrapper.Conditions;
import com.custom.wrapper.LambdaConditionEntity;
import com.home.customtest.entity.Student;

import java.util.Arrays;
import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:54
 * @Desc：
 **/
@SuppressWarnings("all")
public class DoMain {


    public static void main(String[] args) throws Exception {

        // 数据库连接配置
        DbDataSource dbDataSource = new DbDataSource();
        dbDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/hos?characterEncoding=utf-8&allowMultiQueries=true&autoreconnect=true&serverTimezone=UTC");
        dbDataSource.setUsername("root");
        dbDataSource.setPassword("123456");

        // 增删改查映射策略配置
        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
        dbCustomStrategy.setSqlOutPrinting(true);
        dbCustomStrategy.setSqlOutUpdate(true);
        dbCustomStrategy.setSqlOutPrintExecute(true);
        dbCustomStrategy.setUnderlineToCamel(true);
        dbCustomStrategy.setDbFieldDeleteLogic("state");
        dbCustomStrategy.setDeleteLogicValue("1");
        dbCustomStrategy.setNotDeleteLogicValue("0");
//
        CustomDao customDao = new CustomDao(dbDataSource, dbCustomStrategy);

//        List<Location> locations = customDao.selectList(Location.class, new ConditionEntity<>(Location.class).like("name", "区"));

        TableInfoCache.setUnderlineToCamel(true);


//        SqlFunc<Student> dbFunction = new SqlFunc<>(Student.class);
//        dbFunction.avg(Student::getAge).max(Student::getId).min(Student::getCityId).sum(Student::getAge)
//                        .count(Student::getId, true);
//        System.out.println("dbFunction.getSelectColumns() = " + dbFunction.getSelectColumns());

//        Student student = customDao.selectOne(Conditions.lambdaConditionQuery(Student.class)
//                .select(x -> x.sum(Student::getAge)).isNotNull(Student::getArea)
//        );

//        System.out.println("student = " + student);

        long time = System.currentTimeMillis();
        List<Student> students = customDao.selectList(Student.class, Conditions.lambdaConditionQuery(Student.class)
                .select(Student::getAge)
                .select(x -> x.sum(Student::getAge, Student::getArea) )
                .ge(Student::getAge, 22).like(Student::getAddress, "山东")
                .between(Student::getAge, 21, 25)
                .or(x -> x.like(Student::getArea, "哈哈")
                        .orderByAsc(Student::getId)
                        .orderByDesc(Student::getProvince)
                ).or().in(Student::getAreaId, Arrays.asList(1,5,8,9))
                .isNull(Student::getName)
                .or().or().likeLeft(Student::getAddress, "济南")
                        .groupBy(Student::getAge)
        );

        System.out.println("students = " + students);


    }


}
