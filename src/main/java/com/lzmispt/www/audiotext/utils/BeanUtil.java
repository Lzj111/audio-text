package com.lzmispt.www.audiotext.utils;

import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Classname BeanUtil
 * @Description
 * @Date 2022/12/20 15:41
 * @Author by lzj
 */
@Component
public class BeanUtil implements ApplicationContextAware {

    private static ApplicationContext APPLICATION_CONTEXT;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanUtil.APPLICATION_CONTEXT = applicationContext;
    }

    /**
     * @description 根据Bean名称, 获取Bean实例
     * @author cly
     * @date 2021-05-21 15:57
     **/
    public static <T> T getInstanceByBeanName(String beanName) {
        Object bean = APPLICATION_CONTEXT.getBean(beanName);
        return bean == null ? null : (T) bean;
    }

    /**
     * @description 根据Class类名称, 获取所有的Bean实例
     * @author cly
     * @date 2021-05-21 15:57
     **/
    public static <T> Map<String, T> getInstanceByClassName(Class<T> className) {
        Map<String, T> beans = APPLICATION_CONTEXT.getBeansOfType(className);
        return beans;
    }
}
