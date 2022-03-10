package com.home.customtest;

import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.sqlparser.CustomDao;
import com.custom.wrapper.ConditionEntity;
import com.custom.wrapper.ConditionWrapper;
import com.custom.wrapper.LambdaConditionEntity;
import com.home.customtest.entity.City;
import com.home.customtest.entity.Employee;
import com.home.customtest.entity.Location;
import com.home.customtest.entity.Student;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/29 12:54
 * @Desc：
 **/
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
        dbCustomStrategy.setUnderlineToCamel(true);
        dbCustomStrategy.setDbFieldDeleteLogic("state");
        dbCustomStrategy.setDeleteLogicValue("1");
        dbCustomStrategy.setNotDeleteLogicValue("0");
//
        CustomDao customDao = new CustomDao(dbDataSource, dbCustomStrategy);

        List<Location> locations = customDao.selectList(Location.class, new ConditionEntity<>(Location.class).like("name", "区"));

//        List<Student> students = customDao.selectList(Student.class, new LambdaConditionEntity<>(Student.class)
//                .between(Student::getAge, 20, 24).likeLeft(Student::getProvince, "西")
//        );
//        System.out.println("students = " + students);


    }


}
