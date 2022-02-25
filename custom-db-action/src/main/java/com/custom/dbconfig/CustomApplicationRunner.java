package com.custom.dbconfig;

import com.custom.comm.CustomUtil;
import com.custom.enums.ExecuteMethod;
import com.custom.scanner.MapperBeanScanner;
import com.custom.sqlparser.TableParserModelCache;
import com.custom.sqlparser.TableSqlBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author Xiao-Bai
 * @Date 2022/2/24 12:09
 * @Desc：在springboot程序启动后执行一些本地数据缓存操作
 **/
@Component
@Order(7)
public class CustomApplicationRunner implements ApplicationRunner {

    private final DbCustomStrategy dbCustomStrategy;

    public CustomApplicationRunner(DbCustomStrategy dbCustomStrategy){
        this.dbCustomStrategy = dbCustomStrategy;
    }

    @Override
    public void run(ApplicationArguments args) {
        String[] entityScans = dbCustomStrategy.getEntityScans();
        if(entityScans == null) return;

        MapperBeanScanner mapperBeanScanner = new MapperBeanScanner(entityScans);
        Set<Class<?>> entityClassSets = mapperBeanScanner.getBeanRegisterList();
        TableParserModelCache tableParserModelCache = new TableParserModelCache(entityClassSets.size());

        for (Class<?> aClass : entityClassSets) {
            Field[] fields = CustomUtil.getFields(aClass);

            for (ExecuteMethod value : ExecuteMethod.values()) {
                setCache(tableParserModelCache, fields, aClass, value);
            }
        }

    }

    /**
     * 设置缓存
     */
    private void setCache(TableParserModelCache tableParserModelCache, Field[] fields, Class<?> aClass, ExecuteMethod method) {

        TableSqlBuilder<?> tableSqlBuilder = new TableSqlBuilder<>(aClass, method, fields);
        tableParserModelCache.setTableModel(aClass.getSimpleName(), tableSqlBuilder)
                .setKeyModel(aClass.getSimpleName(), tableSqlBuilder.getKeyParserModel())
                .setFieldModel(aClass.getSimpleName(), tableSqlBuilder.getFieldParserModels())
                .setRelatedModel(aClass.getSimpleName(), tableSqlBuilder.getRelatedParserModels());

    }
}
