package com.custom.dbconfig;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/22 11:55
 * @Desc： 手动将对象注入spring
 **/
public class RegisterBeanExecutor implements ApplicationContextAware{

    private ApplicationContext applicationContext;

    public void register(Map<String, Object> beanMap) {

        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) this.applicationContext;
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getAutowireCapableBeanFactory();

        beanMap.forEach((k,v) ->{
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(v.getClass());
            defaultListableBeanFactory.registerBeanDefinition(k, beanDefinitionBuilder.getBeanDefinition());
        });

    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
