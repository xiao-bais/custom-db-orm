package com.custom.scanner;

import com.custom.comm.CustomUtil;
import com.custom.comm.JudgeUtilsAx;
import com.custom.dbconfig.DbCustomStrategy;
import com.custom.dbconfig.DbDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/22 11:55
 * @Desc： 手动将对象注入spring
 **/

@Slf4j
@Component
public class RegisterBeanExecutor implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware{

    private ApplicationContext applicationContext;
    private DbDataSource dbDataSource;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        log.info("------------------->setApplicationContext");
    }

    public RegisterBeanExecutor(DbDataSource dbDataSource) {
        this.dbDataSource = dbDataSource;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        Set<Class<?>> registerBeanSet = getRegisterBeanSet();


        for (Class<?> beanClass : registerBeanSet) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
            GenericBeanDefinition definition = (GenericBeanDefinition) beanDefinitionBuilder.getRawBeanDefinition();
            definition.getPropertyValues().add("interfaceClass", beanClass);
            definition.getPropertyValues().add("typeName", CustomUtil.toIndexLower(beanClass.getSimpleName()));
            definition.getPropertyValues().add("context", applicationContext);
            definition.setBeanClass(beanClass);
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            registry.registerBeanDefinition(beanClass.getName(), definition);
        }


//        for (Class<?> beanClass : registerBeanSet) {
//            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
//            GenericBeanDefinition definition = (GenericBeanDefinition) beanDefinitionBuilder.getRawBeanDefinition();
//
//            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClass);
//            //指定采用ByType的方式来注入容器
//            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
//            registry.registerBeanDefinition(beanClass.getSimpleName(), definition);
//        }


    }


    private Set<Class<?>> getRegisterBeanSet() {
        DbDataSource dbDataSource = applicationContext.getBean(DbDataSource.class);
        DbCustomStrategy dbCustomStrategy = dbDataSource.getDbCustomStrategy();
        String[] packageScans = dbCustomStrategy.getPackageScans();
        if(JudgeUtilsAx.isEmpty(packageScans)) {
            throw new NullPointerException("扫描包路径未配置....");
        }
        return new MapperBeanScanner(packageScans).getBeanRegisterList();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
