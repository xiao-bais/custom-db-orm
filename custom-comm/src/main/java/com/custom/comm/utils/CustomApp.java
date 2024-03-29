package com.custom.comm.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 从spring容器中获取实例bean的工具类
 * @author   Xiao-Bai
 * @since  2022/3/28 15:01
 **/
@Slf4j
@Component
public class CustomApp implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CustomApp.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(Class<T> t) throws NoSuchBeanDefinitionException {
        if(Objects.isNull(applicationContext)) {
            return null;
        }
        return applicationContext.getBean(t);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        T bean;
        try {
            if(Objects.isNull(applicationContext)) {
                return null;
            }
            bean = (T) applicationContext.getBean(beanName);
        }catch (NoSuchBeanDefinitionException e) {
            log.error(e.getMessage(), e);
            return null;
        }
        return bean;
    }


    public static <T> T getBean(Class<T> t, String beanName) {
        if(Objects.isNull(applicationContext)) {
            return null;
        }
        return applicationContext.getBean(beanName, t);
    }


}
