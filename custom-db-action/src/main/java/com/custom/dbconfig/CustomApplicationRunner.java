package com.custom.dbconfig;

import com.custom.comm.CustomUtil;
import com.custom.enums.ExecuteMethod;
import com.custom.scanner.MapperBeanScanner;
import com.custom.sqlparser.TableParserModelCache;
import com.custom.sqlparser.TableSqlBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;

/**
 * @Author Xiao-Bai
 * @Date 2022/2/24 12:09
 * @Desc：在springboot程序启动后执行一些缓存操作
 **/
@Component
@Order(7)
public class CustomApplicationRunner implements CommandLineRunner {

    @Autowired
    private DbCustomStrategy dbCustomStrategy;

    @Override
    public void run(String... args) throws Exception {
        String[] entityScans = dbCustomStrategy.getEntityScans();
        if(entityScans == null) return;
        MapperBeanScanner mapperBeanScanner = new MapperBeanScanner(entityScans);
        Set<Class<?>> beanRegisterList = mapperBeanScanner.getBeanRegisterList();
        TableParserModelCache tableParserModelCache = new TableParserModelCache(beanRegisterList.size());
        for (Class<?> aClass : beanRegisterList) {
            Field[] fields = CustomUtil.getFields(aClass);
            TableSqlBuilder<?> tableSqlBuilder1 = new TableSqlBuilder<>(aClass, ExecuteMethod.NONE);
            tableParserModelCache.setTableModel(aClass.getSimpleName(), tableSqlBuilder1)
                    .setKeyModel(aClass.getSimpleName(), tableSqlBuilder1.getKeyParserModel())
                    .setFieldModel(aClass.getSimpleName(), tableSqlBuilder1.getFieldParserModels())
                    .setRelatedModel(aClass.getSimpleName(), tableSqlBuilder1.getRelatedParserModels());

            TableSqlBuilder<?> tableSqlBuilder2 = new TableSqlBuilder<>(aClass, ExecuteMethod.DELETE, fields);
            tableParserModelCache.setTableModel(aClass.getSimpleName(), tableSqlBuilder2)
                    .setKeyModel(aClass.getSimpleName(), tableSqlBuilder2.getKeyParserModel())
                    .setFieldModel(aClass.getSimpleName(), tableSqlBuilder2.getFieldParserModels())
                    .setRelatedModel(aClass.getSimpleName(), tableSqlBuilder2.getRelatedParserModels());

            TableSqlBuilder<?> tableSqlBuilder3 = new TableSqlBuilder<>(aClass, ExecuteMethod.UPDATE, fields);
            tableParserModelCache.setTableModel(aClass.getSimpleName(), tableSqlBuilder3)
                    .setKeyModel(aClass.getSimpleName(), tableSqlBuilder3.getKeyParserModel())
                    .setFieldModel(aClass.getSimpleName(), tableSqlBuilder3.getFieldParserModels())
                    .setRelatedModel(aClass.getSimpleName(), tableSqlBuilder3.getRelatedParserModels());

            TableSqlBuilder<?> tableSqlBuilder4 = new TableSqlBuilder<>(aClass);

            tableParserModelCache.setTableModel(aClass.getSimpleName(), tableSqlBuilder4)
                    .setKeyModel(aClass.getSimpleName(), tableSqlBuilder4.getKeyParserModel())
                    .setFieldModel(aClass.getSimpleName(), tableSqlBuilder4.getFieldParserModels())
                    .setRelatedModel(aClass.getSimpleName(), tableSqlBuilder4.getRelatedParserModels());
        }

    }
}
