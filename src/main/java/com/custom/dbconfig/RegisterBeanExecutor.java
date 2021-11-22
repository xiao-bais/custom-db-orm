package com.custom.dbconfig;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/22 11:55
 * @Desc： 手动将对象注入spring
 **/
public class RegisterBeanExecutor implements ApplicationContextAware {

    private Map<String, Object> beanMap;

    @Autowired
    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public RegisterBeanExecutor(Map<String, Object> beanMap) {
        this.beanMap = beanMap;
    }

    public void register() {

        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) this.applicationContext;
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getAutowireCapableBeanFactory();

        beanMap.forEach((k,v) ->{
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(v.getClass());
            defaultListableBeanFactory.registerBeanDefinition(k, beanDefinitionBuilder.getBeanDefinition());
        });

    }




}
