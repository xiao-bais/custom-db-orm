package com.home.customtest;

import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.sqlparser.CustomDao;
import com.custom.sqlparser.TableInfoCache;
import com.custom.wrapper.LambdaConditionEntity;
import com.home.customtest.entity.Student;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

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
//        dbCustomStrategy.setSqlOutPrintExecute(true);
        dbCustomStrategy.setUnderlineToCamel(true);
        dbCustomStrategy.setDbFieldDeleteLogic("state");
        dbCustomStrategy.setDeleteLogicValue("1");
        dbCustomStrategy.setNotDeleteLogicValue("0");
//
        CustomDao customDao = new CustomDao(dbDataSource, dbCustomStrategy);

//        List<Location> locations = customDao.selectList(Location.class, new ConditionEntity<>(Location.class).like("name", "区"));

        TableInfoCache.setUnderlineToCamel(true);


        List<Student> students1 = customDao.selectList(Student.class, new LambdaConditionEntity<>(Student.class)
                .ge(Student::getAge, 22).like(Student::getAddress, "山东")
                .between(Student::getAge, 21, 25)
                .select(Student::getName, Student::getProvince, Student::getCity, Student::getArea)
                .or(x -> x.select(Student::getAge)
                        .exists("select 1 from student2 stu2 where stu2.id = a.id and stu2.password = '12345678zcy'")
                        .like(Student::getArea, "哈哈")
                        .orderByAsc(Student::getId)
                        .orderByDesc(Student::getProvince)
                ).or().in(Student::getAreaId, Arrays.asList(1,5,8,9))
                .isNull(Student::getName)
                .and(new LambdaConditionEntity<>(Student.class))
                .or().or().or().likeLeft(Student::getAddress, "济南")
        );

        System.out.println("students1 = " + students1);

    }


}
