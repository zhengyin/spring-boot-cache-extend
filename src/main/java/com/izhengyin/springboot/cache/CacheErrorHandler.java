package com.izhengyin.springboot.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

/**
 * @author zhengyin  zhengyinit@outlook.com
 * @date Created on 2019-11-07 10:22
 */
public class CacheErrorHandler implements org.springframework.cache.interceptor.CacheErrorHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheTtlConfig.class);
    @Override
    public void handleCacheGetError(RuntimeException e, Cache cache, Object o) {
        LOGGER.error("handleCacheGetError {} , {}",e.getClass().getName(),e.getMessage());
    }

    @Override
    public void handleCachePutError(RuntimeException e, Cache cache, Object o, Object o1) {
        LOGGER.error("handleCachePutError {} , {}",e.getClass().getName(),e.getMessage());
    }

    @Override
    public void handleCacheEvictError(RuntimeException e, Cache cache, Object o) {
        LOGGER.error("handleCacheEvictError {} , {}",e.getClass().getName(),e.getMessage());
    }

    @Override
    public void handleCacheClearError(RuntimeException e, Cache cache) {
        LOGGER.error("handleCacheClearError {} , {}",e.getClass().getName(),e.getMessage());
    }
}