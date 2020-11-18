package com.izhengyin.springboot.cache.constant;

/**
 * @author zhengyin  zhengyinit@outlook.com
 * @date Created on 2019-11-07 10:22
 */
public class CacheName {
    /**
     * Redis 缓存
     */
    public interface Redis {
        String TTL_1 = "TTL_1";
        String TTL_5 = "TTL_5";
        String TTL_60 = "TTL_60";
        String TTL_180 = "TTL_180";
        String TTL_300 = "TTL_300";
        String TTL_600 = "TTL_600";
        String TTL_1800 = "TTL_1800";
        String TTL_3600 = "TTL_3600";
        String TTL_10800 = "TTL_10800";
        String TTL_21600 = "TTL_21600";
        String TTL_43200 = "TTL_43200";
        String TTL_86400 = "TTL_86400";
        String TTL_3_DAY = "TTL_3_DAY";
        String TTL_7_DAY = "TTL_7_DAY";
    }
    /**
     * CAFFEINE 本地缓存
     */
    public interface Caffeine{
        String TTL_1 = "TTL_1";
        String TTL_5 = "TTL_5";
        String TTL_60 = "TTL_60";
        String TTL_300 = "TTL_300";
        String TTL_600 = "TTL_600";
        String TTL_3600 = "TTL_3600";
        String TTL_86400 = "TTL_86400";
    }
}