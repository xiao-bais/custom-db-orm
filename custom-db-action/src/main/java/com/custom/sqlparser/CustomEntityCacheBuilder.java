package com.custom.sqlparser;

import com.custom.dbconfig.DbCustomStrategy;
import com.custom.scanner.CustomBeanScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @Author Xiao-Bai
 * @Date 2022/2/24 12:09
 * @Desc：在springboot程序启动后执行一些本地数据缓存操作
 **/
public class CustomEntityCacheBuilder {

    private static final Logger logger = LoggerFactory.getLogger(CustomEntityCacheBuilder.class);

    private final DbCustomStrategy dbCustomStrategy;

    public CustomEntityCacheBuilder(DbCustomStrategy dbCustomStrategy){
        this.dbCustomStrategy = dbCustomStrategy;
    }

    /**
     *
     */
    public void buildEntity() {
        String[] entityScans = dbCustomStrategy.getEntityScans();
        if(entityScans == null || entityScans.length == 0) return;

        CustomBeanScanner mapperBeanScanner = new CustomBeanScanner(entityScans);
        Set<Class<?>> entityClassSets = mapperBeanScanner.getBeanRegisterList();
        TableParserModelCache tableParserModelCache = new TableParserModelCache(entityClassSets.size());

        // 设置表的实体缓存
        entityClassSets.forEach(cls -> tableParserModelCache.setTableModel(cls.getSimpleName(), new TableSqlBuilder<>(cls)));
        dbCustomStrategy.setTableParserModelCache(tableParserModelCache);
        logger.info("Entity parse model Initialized Completed ... ...");

    }
}
