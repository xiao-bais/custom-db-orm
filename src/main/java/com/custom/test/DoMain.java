package com.custom.test;

import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.handler.JdbcDao;

import java.util.List;

/**
 * @Author Xiao-Bai
 * @Date 2021/7/4
 * @Description
 */
public class DoMain {



    public static void main(String[] args) throws Exception {

        DbDataSource dbDataSource = new DbDataSource();
        dbDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/oneTest?characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8");
        dbDataSource.setDriver("com.mysql.cj.jdbc.Driver");
        dbDataSource.setUsername("root");
        dbDataSource.setPassword("123456");
        DbCustomStrategy dbCustomStrategy = new DbCustomStrategy();
        dbCustomStrategy.setSqlOutPrinting(true);
        dbCustomStrategy.setUnderlineToCamel(true);
        dbDataSource.setDbCustomStrategy(dbCustomStrategy);

        JdbcDao jdbcDao = new JdbcDao(dbDataSource);

        String sql = "select a.`stu_id`, a.`name` `name`, a.`age` `age`, a.`sex` `sex`, a.`birthDay` `birthDay`, a.`explain` `explain`, a.`cls_id` `clsId`, a.`teach_id` `teachId` ,cls.`clsName` `className`,t.`leader_name` `leaderName` \n" +
                "from student a  \n" +
                "left join classes cls on cls.clsId = a.cls_id\n" +
                "left join leader t on t.id = a.teach_id";

        List<Person> personList = jdbcDao.selectListBySql(Person.class, sql);
        System.out.println(1);


    }
}
