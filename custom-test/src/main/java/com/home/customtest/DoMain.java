package com.home.customtest;

import com.custom.comm.page.DbPageRows;
import com.custom.dbaction.AbstractSqlBuilder;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import com.custom.dbconfig.SymbolConst;
import com.custom.enums.FillStrategy;
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
import java.util.ArrayList;
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
        dbCustomStrategy.setDeleteLogicValue(1);
        dbCustomStrategy.setNotDeleteLogicValue(0);

        CustomDao customDao = new CustomDao(dbDataSource, dbCustomStrategy);
        TableInfoCache.setUnderlineToCamel(true);

        AutoFillColumnHandler autoFillColumnHandler = new CustomFillConfig();
        List<TableFillObject> tableFillObjects = autoFillColumnHandler.fillStrategy();


//        int i = customDao.deleteByCondition(Conditions.lambdaQuery(Aklis.class).eq(Aklis::getExplain, "张三"));

        Aklis aklis = new Aklis();
        aklis.setId(239);
        aklis.setName("马回峰");
        aklis.setAddress("湖南长沙");
        aklis.setAge(23);
        aklis.setExplain("aaaa");

        customDao.updateByCondition(aklis, Conditions.lambdaQuery(Aklis.class).isNotNull(Aklis::getExplain).like(Aklis::getName, "马回峰"));
    }



}
