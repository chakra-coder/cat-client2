package com.patterncat.apm.service;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by patterncat on 2016-09-08.
 */
public class ApmApplicationContextHolder implements ApplicationContextAware {

    private static ApplicationContext ctx;

    public static <T> T getBean(Class<T> clz){
        if(ctx == null){
            return null;
        }
        return ctx.getBean(clz);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }
}
