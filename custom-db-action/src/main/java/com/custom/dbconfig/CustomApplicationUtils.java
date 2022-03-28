package com.custom.dbconfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/28 15:01
 * @Desc：从spring容器中获取实例bean的工具类
 **/
@Component
@Slf4j
public class CustomApplicationUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CustomApplicationUtils.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(Class<T> t) {
        T bean;
        try {
            bean = applicationContext.getBean(t);
        }catch (NoSuchBeanDefinitionException e) {
            log.error(e.getMessage(), e);
            return null;
        }
        return bean;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        T bean;
        try {
            bean = (T) applicationContext.getBean(beanName);
        }catch (NoSuchBeanDefinitionException e) {
            log.error(e.getMessage(), e);
            return null;
        }
        return bean;
    }


    public static <T> T getBean(Class<T> t, String beanName) {
        return (T) applicationContext.getBean(beanName, t);
    }


}
