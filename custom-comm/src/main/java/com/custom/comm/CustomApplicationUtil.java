package com.custom.comm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @Author Xiao-Bai
 * @Date 2022/3/28 15:01
 * @Desc：从spring容器中获取实例bean的工具类
 **/
@Component
@Slf4j
public class CustomApplicationUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CustomApplicationUtil.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(Class<T> t) {
        T bean;
        try {
            if(Objects.isNull(applicationContext)) {
                return null;
            }
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
