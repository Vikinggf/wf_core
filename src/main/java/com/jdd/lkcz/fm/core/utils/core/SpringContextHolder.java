//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jdd.lkcz.fm.core.utils.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.PropertyResolver;

public class SpringContextHolder implements ApplicationContextAware, DisposableBean {
    private static Logger logger = LoggerFactory.getLogger(SpringContextHolder.class);
    private static ApplicationContext applicationContext;
    private static PropertyResolver propertyResolver;
    private static BeanDefinitionRegistry beanDefinitionRegistry;

    public SpringContextHolder() {
    }

    public String getProperty(String key) {
        check();
        return propertyResolver.getProperty(key);
    }

    public static boolean containsProperty(String key) {
        check();
        return propertyResolver.containsProperty(key);
    }

    public static <T> T getBean(String name) {
        check();
        return (T) applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) {
        check();
        return applicationContext.getBean(requiredType);
    }

    public static ApplicationContext getApplicationContext() {
        check();
        return applicationContext;
    }

    public static void registerBeanDefinition(Class<?> clazz) {
        if (beanDefinitionRegistry == null) {
            check();
            AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
            if (!(factory instanceof BeanDefinitionRegistry)) {
                throw new SpringContextHolderException("applicationContext.getAutowireCapableBeanFactory()获取的实例无法转换为BeanDefinitionRegistry");
            }

            beanDefinitionRegistry = (BeanDefinitionRegistry)factory;
        }

        registerBeanDefinition(beanDefinitionRegistry, clazz);
    }

    private static void registerBeanDefinition(BeanDefinitionRegistry beanDefinitionRegistry, Class<?> clazz) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(clazz);
        String className = clazz.getSimpleName();
        String beanName = Character.toUpperCase(className.charAt(0)) + className.substring(1);
        beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        if (applicationContext == null) {
            logger.debug("SpringContextHolder.applicationContext初始化");
        } else {
            logger.warn("SpringContextHolder.applicationContext被覆盖");
        }

        applicationContext = applicationContext;
        propertyResolver = (PropertyResolver)getBean(PropertyResolver.class);
    }

    public void destroy() throws Exception {
        applicationContext = null;
        beanDefinitionRegistry = null;
        logger.warn("SpringContextHolder.applicationContext被清除");
    }

    private static void check() {
        if (applicationContext == null) {
            throw new NullPointerException("未持有applicationContext");
        }
    }
}
