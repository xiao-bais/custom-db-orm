package com.home.customtest;

import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.dbconfig.SymbolConst;
import com.custom.sqlparser.CustomDao;
import com.custom.sqlparser.TableInfoCache;
import com.custom.wrapper.Conditions;
import com.custom.wrapper.LambdaConditionEntity;
import com.home.customtest.entity.Aklis;
import com.home.customtest.entity.ChildStudent;
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
        dbCustomStrategy.setSqlOutUpdate(true);
        dbCustomStrategy.setSqlOutPrintExecute(true);
        dbCustomStrategy.setUnderlineToCamel(true);
        dbCustomStrategy.setDbFieldDeleteLogic("state");
        dbCustomStrategy.setDeleteLogicValue("1");
        dbCustomStrategy.setNotDeleteLogicValue("0");
//
        CustomDao customDao = new CustomDao(dbDataSource, dbCustomStrategy);
        TableInfoCache.setUnderlineToCamel(true);

//        AutoFillColumnHandler autoFillColumnHandler = new CustomFillConfig();
//        List<TableFillObject> tableFillObjects = autoFillColumnHandler.fillStrategy();
//        for (TableFillObject fillObject : tableFillObjects) {
//            TableInfoCache.setTableFill(Aklis.class.getName(), fillObject);
//        }


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
//        long time = System.currentTimeMillis();
//        List<ChildStudent> students = customDao.selectList(ChildStudent.class, Conditions.lambdaQuery(ChildStudent.class)
//                        .gt(ChildStudent::getAge, 22)
//                        .eq(ChildStudent::getArea, "aaa")
//                        .like(ChildStudent::getAddress, "山东")
//                        .or().ge(ChildStudent::getAge, 23)
//                        .le(ChildStudent::getId, 10)
//                        .or(x -> x.exists("select 1 from student stu2 where stu2.address is not null")
//                                .or().eq(ChildStudent::getSex, false)
//                                .or().like(ChildStudent::getAddress, "南")
//                        )
//                        .orderByDesc(ChildStudent::getId)
//        );



                //待匹配的字符串："Hello"Hi"Nice"Good
                String content=" aaa #{dd} da";
                System.out.println(content);
                //匹配双引号的正则表达式
                String pattStr = "(?<=#\\{).*?(?=\\})";
                //创建Pattern并进行匹配
                Pattern pattern= Pattern.compile(pattStr);
                Matcher matcher=pattern.matcher(content);
                //将所有匹配的结果打印输出
                System.out.println(matcher.find());


    }



}
