package com.izhengyin.springboot.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.izhengyin.springboot.cache.constant.CacheName;
import org.springframework.cache.caffeine.CaffeineCache;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zhengyin  zhengyinit@outlook.com
 * @date Created on 2019-11-08 14:23
 */
public class CaffeineCacheTtlConfig {
    public static List<CaffeineCache> getCacheNameConfigs(){
        CaffeineCache TTL_1 = new CaffeineCache(CacheName.Caffeine.TTL_1,
                Caffeine.newBuilder().
                        expireAfterWrite(1, TimeUnit.SECONDS)
                        .maximumSize(10000)
                        .build());

        CaffeineCache TTL_5 = new CaffeineCache(CacheName.Caffeine.TTL_5,
                Caffeine.newBuilder().
                        expireAfterWrite(5, TimeUnit.SECONDS)
                        .maximumSize(10000)
                        .build());

        CaffeineCache TTL_60 = new CaffeineCache(CacheName.Caffeine.TTL_60,
                Caffeine.newBuilder().
                        expireAfterWrite(60, TimeUnit.SECONDS)
                        .maximumSize(10000)
                        .build());

        CaffeineCache TTL_300 = new CaffeineCache(CacheName.Caffeine.TTL_300,
                Caffeine.newBuilder().
                        expireAfterWrite(300, TimeUnit.SECONDS)
                        .maximumSize(10000)
                        .build());
        CaffeineCache TTL_600 = new CaffeineCache(CacheName.Caffeine.TTL_600,
                Caffeine.newBuilder().
                        expireAfterWrite(600, TimeUnit.SECONDS)
                        .maximumSize(10000)
                        .build());
        CaffeineCache TTL_3600 = new CaffeineCache(CacheName.Caffeine.TTL_3600,
                Caffeine.newBuilder().
                        expireAfterWrite(3600, TimeUnit.SECONDS)
                        .maximumSize(10000)
                        .build());

        CaffeineCache TTL_86400 = new CaffeineCache(CacheName.Caffeine.TTL_86400,
                Caffeine.newBuilder().
                        expireAfterWrite(86400, TimeUnit.SECONDS)
                        .maximumSize(10000)
                        .build());

        return Arrays.asList(TTL_1,TTL_5,TTL_60,TTL_300,TTL_600,TTL_3600,TTL_86400);
    }
}
