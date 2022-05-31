package com.custom.springboot.scanner;

import com.custom.comm.JudgeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
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


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {

        Environment environment = applicationContext.getEnvironment();
        Boolean mapperScanEnable = environment.getProperty("custom.db.strategy.mapper-scan-enable", Boolean.class);

        if(mapperScanEnable == null || !mapperScanEnable) {
            return;
        }
        String[] packageScans = environment.getProperty("custom.db.strategy.package-scans", String[].class);
        if(JudgeUtil.isEmpty(packageScans)) {
            return;
        }

        PackageScanner packageScanner = new PackageScanner(packageScans);
        Set<Class<?>> beanRegisterList = packageScanner.getBeanRegisterList();

        for (Class<?> beanClass : beanRegisterList) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
            GenericBeanDefinition rawBeanDefinition = (GenericBeanDefinition) beanDefinitionBuilder.getRawBeanDefinition();
            rawBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanClass);

            rawBeanDefinition.setBeanClass(InstanceBeanFactory.class);
            beanDefinitionRegistry.registerBeanDefinition(beanClass.getSimpleName(), rawBeanDefinition);
        }

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }



}
