package com.izhengyin.springboot.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * @author zhengyin  zhengyinit@outlook.com
 * @date Created on 2019-11-07 10:22
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CacheTarget {
    Class<?> value() default Object.class;
    String method() default "";
}