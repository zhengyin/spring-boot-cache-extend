package com.izhengyin.springboot.cache;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izhengyin.springboot.cache.annotation.CacheTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author zhengyin  zhengyinit@outlook.com
 * @date Created on 2019-11-07 11:45
 */
public class CachingConfigurer extends CachingConfigurerSupport {
    private final static Logger LOGGER = LoggerFactory.getLogger(CachingConfigurer.class);
    private final String application;

    public CachingConfigurer(String application ){
        this.application = application;
    }

    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(application);
            sb.append(":");
            sb.append(getKey(target,method,params));
            return sb.toString();
        };
    }


    @Override
    public org.springframework.cache.interceptor.CacheErrorHandler errorHandler() {
        return new CacheErrorHandler();
    }

    /**
     * 组合key
     * @param target
     * @param method
     * @param params
     * @return
     */
    private String getKey(Object target, Method method, Object[] params) {
        StringBuilder sb = new StringBuilder();
        CacheTarget cacheTarget = method.getAnnotation(CacheTarget.class);
        if(cacheTarget != null){
            if(!cacheTarget.value().getName().equals(Object.class.getName())){
                sb.append(cacheTarget.value().getSimpleName());
            }else{
                sb.append(target.getClass().getSimpleName());
            }
            sb.append(".");
            if(!"".equals(cacheTarget.method())){
                sb.append(cacheTarget.method());
            }else{
                sb.append(method.getName());
            }
        }else{
            sb.append(target.getClass().getSimpleName());
            sb.append(".")
                    .append(method.getName());
        }
        ObjectMapper mapper = new ObjectMapper();
        for (Object obj : params) {

            if(Objects.isNull(obj)){
                continue;
            }

            if (obj instanceof Supplier){
                continue;
            }
            sb.append(":");
            if(obj instanceof Map){
                try {
                    sb.append(mapper.writeValueAsString(params));
                }catch (JsonProcessingException e){
                    LOGGER.error("JsonProcessingException "+e.getMessage() , e);
                }catch (RuntimeException e){
                    LOGGER.error("DefaultKeyGenerator mapper.writeValueAsString "+e.getMessage(),e);
                }
            }else {
                sb.append(obj.toString());
            }
        }
        return sb.toString();
    }



}
