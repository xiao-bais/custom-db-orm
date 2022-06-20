package com.custom.springboot.scanner;

import com.custom.comm.JudgeUtil;
import com.custom.configuration.DbCustomStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @Author Xiao-Bai
 * @Date 2021/11/22 11:55
 * @Desc： 手动将对象注入spring
 **/

@Slf4j
@Component
public class RegisterBeanExecutor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private Environment environment;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {

        String[] packageScans = environment.getProperty("custom.db.strategy.mapper-package-scans", String[].class);
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
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
