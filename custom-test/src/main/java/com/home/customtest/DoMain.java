package com.home.customtest;

import com.custom.comm.page.DbPageRows;
import com.custom.dbaction.AbstractSqlBuilder;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.dbconfig.SymbolConst;
import com.custom.fieldfill.AutoFillColumnHandler;
import com.custom.fieldfill.TableFillObject;
import com.custom.sqlparser.*;
import com.custom.wrapper.ConditionEntity;
import com.custom.wrapper.Conditions;
import com.custom.wrapper.LambdaConditionEntity;
import com.home.customtest.config.CustomFillConfig;
import com.home.customtest.entity.Aklis;
import com.home.customtest.entity.ChildStudent;
import com.home.customtest.entity.Student;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        dbDataSource.setUrl("jdbc:mysql://39.108.225.176:3306/hos?characterEncoding=utf-8&allowMultiQueries=true&autoreconnect=true&serverTimezone=UTC");
        dbDataSource.setUsername("root");
        dbDataSource.setPassword("xh@Mysql1524");

        // 增删改查映射策略配置
        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
        dbCustomStrategy.setSqlOutPrinting(true);
        dbCustomStrategy.setSqlOutPrintExecute(true);
        dbCustomStrategy.setUnderlineToCamel(true);
        dbCustomStrategy.setDbFieldDeleteLogic("state");
        dbCustomStrategy.setDeleteLogicValue("1");
        dbCustomStrategy.setNotDeleteLogicValue("0");

        CustomDao customDao = new CustomDao(dbDataSource, dbCustomStrategy);
        TableInfoCache.setUnderlineToCamel(true);

        AutoFillColumnHandler autoFillColumnHandler = new CustomFillConfig();
        List<TableFillObject> tableFillObjects = autoFillColumnHandler.fillStrategy();


//        Aklis aklis = new Aklis();
//        aklis.setId(10);
//        aklis.setName("张超艾");
//        aklis.setAddress("河南商丘");
//        customDao.insert(aklis);

//        List<Location> locations = customDao.selectList(Location.class, new ConditionEntity<>(Location.class).like("name", "区"));




//        List<Student> students = customDao.selectList(Student.class, " and a.age > ?", 22);
//        DbPageRows<Student> pageRows = customDao.selectPageRows(Student.class, " and a.age > ?", 1, 10, 22);

//        long sex = customDao.selectCount(Conditions.conditionQuery(Student.class).eq("sex", true));
//        System.out.println("sex = " + sex);

//        SqlFunc<Student> dbFunction = new SqlFunc<>(Student.class);
//        dbFunction.avg(Student::getAge).max(Student::getId).min(Student::getCityId).sum(Student::getAge)
//                        .count(Student::getId, true);
//        System.out.println("dbFunction.getSelectColumns() = " + dbFunction.getSelectColumns());

//        Student student = customDao.selectOne(Conditions.lambdaConditionQuery(Student.class)
//                .select(x -> x.sum(Student::getAge)).isNotNull(Student::getArea)
//        );

//        System.out.println("student = " + student);

//        Student student = new Student();
//        List<ChildStudent> students = customDao.selectList(ChildStudent.class, Conditions.lambdaQuery(ChildStudent.class)
//                        .select(ChildStudent::getAge)
//                        .select(x -> x.count(ChildStudent::getAge, ChildStudent::getCountAge))
//                        .gt(ChildStudent::getAge, 22)
//                        .eq(ChildStudent::getArea, "aaa")
//                        .like(ChildStudent::getAddress, "山东")
//                        .or().ge(ChildStudent::getAge, 23)
//                        .le(ChildStudent::getId, 10)
//                        .or(x -> x.exists("select 1 from student stu2 where stu2.address is not null")
//                                .or().eq(ChildStudent::getSex, false)
//                                .or().like(ChildStudent::getAddress, "南")
//                        )
//                .groupBy(ChildStudent::getAge)
////                        .orderByDesc(ChildStudent::getId)
//                        .orderByAsc(x -> x.count(ChildStudent::getAge))
//        );
//        System.out.println("students = " + students);

//        ConditionEntity<ChildStudent> query = Conditions.query(ChildStudent.class);
//        query.eq("", "林涂").eq("pro.name", "湖南");
//        List<ChildStudent> childStudents = customDao.selectList(ChildStudent.class, query);
//        System.out.println("childStudents = " + childStudents);

        int i = customDao.deleteByKey(ChildStudent.class, 239);


    }



}
