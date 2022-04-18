package com.home.customtest;

import com.custom.action.dbconfig.DbCustomStrategy;
import com.custom.action.dbconfig.DbDataSource;
import com.custom.action.fieldfill.AutoFillColumnHandler;
import com.custom.action.fieldfill.TableFillObject;
import com.custom.action.sqlparser.JdbcDao;
import com.custom.action.sqlparser.TableInfoCache;
import com.custom.action.wrapper.Conditions;
import com.home.customtest.config.CustomFillConfig;
import com.home.customtest.entity.*;

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
        dbCustomStrategy.setSqlOutPrintExecute(true);
        dbCustomStrategy.setUnderlineToCamel(true);
        dbCustomStrategy.setDbFieldDeleteLogic("state");
        dbCustomStrategy.setDeleteLogicValue(1);
        dbCustomStrategy.setNotDeleteLogicValue(0);

        JdbcDao jdbcDao = new JdbcDao(dbDataSource, dbCustomStrategy);
        TableInfoCache.setUnderlineToCamel(true);

        AutoFillColumnHandler autoFillColumnHandler = new CustomFillConfig();
        List<TableFillObject> tableFillObjects = autoFillColumnHandler.fillStrategy();


//        int i = customDao.deleteByCondition(Conditions.lambdaQuery(Aklis.class).eq(Aklis::getExplain, "张三"));

        Aklis aklis = new Aklis();
        aklis.setId(239);
        aklis.setName("马回峰");
        aklis.setAddress("湖南长沙111");
        aklis.setAge(23);
        aklis.setExplain("bbb");

//        List<Object> objects = customDao.selectObjs(Conditions.lambdaQuery(Aklis.class).like(Aklis::getName, "李")
//                .select(x -> x.ifNull(Aklis::getExplain, "未知"))
//        );
//        System.out.println("objects = " + objects);

        Aklis aklis1 = new Aklis();
        aklis1.setAddress("那就SV的");
        int i = jdbcDao.updateByCondition(aklis1, Conditions.lambdaQuery(Aklis.class).eq(Aklis::getName, "张三"));

        System.out.println("结束");




//        customDao.updateByCondition(aklis, Conditions.lambdaQuery(Aklis.class).isNotNull(Aklis::getExplain).like(Aklis::getName, "马回峰"));
    }



}
