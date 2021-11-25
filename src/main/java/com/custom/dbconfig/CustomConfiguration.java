package com.custom.dbconfig;

import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.exceptions.ExceptionConst;
import com.custom.handler.proxy.SqlReaderExecuteProxy;
//import com.custom.scanner.RegisterBeanExecutor;
import com.custom.scanner.MapperBeanScanner;
import com.custom.scanner.RegisterBeanExecutor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/23 17:47
 * @Desc：
 **/
@Configuration
public class CustomConfiguration {

    private DbDataSource dbDataSource;

    public CustomConfiguration(DbDataSource dbDataSource) {
        this.dbDataSource = dbDataSource;
    }

    @Bean
    @ConditionalOnBean(DbDataSource.class)
    public SqlReaderExecuteProxy sqlReaderExecuteProxy() {
        if(!CustomUtil.isDataSourceEmpty(dbDataSource)) {
            return new SqlReaderExecuteProxy(dbDataSource);
        }
        return null;
    }



//    @Bean
//    public RegisterBeanExecutor registerBeanExecutor(){
//        return new RegisterBeanExecutor(dbDataSource);
//    }


}
