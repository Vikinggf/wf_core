package com.wf.core.db;

import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class DataSourceExchange implements MethodBeforeAdvice, AfterReturningAdvice {

    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        DataSourceContext.clearCustomerType();
    }
    public void before(Method method, Object[] args, Object target) throws Throwable {
        //这里DataSource是自定义的注解，不是java里的DataSource接口
        if (method.isAnnotationPresent(DataSource.class)) {
            DataSource datasource = method.getAnnotation(DataSource.class);
            DataSourceContext.setCustomerType(datasource.name());
        } else {
            DataSourceContext.setCustomerType(DataSourceContext.DATA_SOURCE_WRITE);
        }
    }


}  
