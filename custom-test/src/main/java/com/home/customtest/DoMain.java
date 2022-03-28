package com.home.customtest;

import com.custom.comm.page.DbPageRows;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.fill.AutoFillColumnHandler;
import com.custom.fill.TableFillObject;
import com.custom.sqlparser.CustomDao;
import com.custom.sqlparser.TableInfoCache;
import com.custom.wrapper.Conditions;
import com.custom.wrapper.LambdaConditionEntity;
import com.home.customtest.config.CustomFillConfig;
import com.home.customtest.entity.Aklis;
import com.home.customtest.entity.Street;
import com.home.customtest.entity.Student;

import java.math.BigDecimal;
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
        dbDataSource.setUrl("jdbc:mysql://39.108.225.176:3306/hos?characterEncoding=utf-8&allowMultiQueries=true&autoreconnect=true&serverTimezone=UTC");
        dbDataSource.setUsername("root");
        dbDataSource.setPassword("xh@Mysql1524");

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
        List<Student> students = customDao.selectList(Student.class, Conditions.lambdaConditionQuery(Student.class)
                        .gt(Student::getAge, 22)
                        .eq(Student::getArea, "aaa")
                        .like(Student::getAddress, "山东")
                        .or().ge(Student::getAge, 23)
                        .le(Student::getId, 10)
                        .or(x -> x.exists("select 1 from student stu2 where stu2.address is not null")
                                .eq(Student::getSex, false)
                        )
                        .orderByDesc(Student::getId)
        );

        System.out.println("students = " + students);

    }


}
