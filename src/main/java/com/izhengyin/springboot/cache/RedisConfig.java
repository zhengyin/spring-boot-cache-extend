package com.izhengyin.springboot.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izhengyin.springboot.cache.constant.CacheName;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhengyin  zhengyinit@outlook.com
 * @date Created on 2019-11-06 17:22
 */
public class RedisConfig {

    private final static Logger LOGGER = LoggerFactory.getLogger(RedisConfig.class);

    /**
     * 获取redis缓存时间配置
     * @return
     */
    static Map<String, RedisCacheConfiguration> getRedisCacheConfigurationMap() {
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>(20);
        /**
         * 缓存1秒
         */
        redisCacheConfigurationMap.put(CacheName.Redis.TTL_1, getRedisCacheConfigurationWithTtl(1));
        /**
         * 缓存5秒
         */
        redisCacheConfigurationMap.put(CacheName.Redis.TTL_5, getRedisCacheConfigurationWithTtl(5));
        /**
         * 缓存1分钟
         */
        redisCacheConfigurationMap.put(CacheName.Redis.TTL_60, getRedisCacheConfigurationWithTtl(60));
        /**
         * 缓存3分钟
         */
        redisCacheConfigurationMap.put(CacheName.Redis.TTL_180, getRedisCacheConfigurationWithTtl(180));
        /**
         * 缓存5分钟
         */
        redisCacheConfigurationMap.put(CacheName.Redis.TTL_300, getRedisCacheConfigurationWithTtl(300));
        /**
         * 缓存10分钟
         */
        redisCacheConfigurationMap.put(CacheName.Redis.TTL_600, getRedisCacheConfigurationWithTtl(600));
        /**
         * 缓存30分钟
         */
        redisCacheConfigurationMap.put(CacheName.Redis.TTL_1800, getRedisCacheConfigurationWithTtl(1800));
        /**
         * 缓存1小时
         */
        redisCacheConfigurationMap.put(CacheName.Redis.TTL_3600, getRedisCacheConfigurationWithTtl(3600));
        /**
         * 缓存3小时
         */
        redisCacheConfigurationMap.put(CacheName.Redis.TTL_10800, getRedisCacheConfigurationWithTtl(10800));
        //
        /**
         * 缓存6小时
         */
        redisCacheConfigurationMap.put(CacheName.Redis.TTL_21600, getRedisCacheConfigurationWithTtl(21600));
        /**
         * 缓存12小时
         */
        redisCacheConfigurationMap.put(CacheName.Redis.TTL_43200, getRedisCacheConfigurationWithTtl(43200));
        /**
         * 缓存1天
         */
        redisCacheConfigurationMap.put(CacheName.Redis.TTL_86400, getRedisCacheConfigurationWithTtl(86400));

        /**
         * 缓存3天
         */
        redisCacheConfigurationMap.put(CacheName.Redis.TTL_3_DAY, getRedisCacheConfigurationWithTtl(86400 * 3));
        /**
         * 缓存7天
         */
        redisCacheConfigurationMap.put(CacheName.Redis.TTL_7_DAY, getRedisCacheConfigurationWithTtl(86400 * 7));

        return redisCacheConfigurationMap;
    }

    /**
     * 获取缓存时间配置
     * @param seconds
     * @return
     */
    static RedisCacheConfiguration getRedisCacheConfigurationWithTtl(Integer seconds) {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        redisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(
                RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(jackson2JsonRedisSerializer)
        ).entryTtl(Duration.ofSeconds(seconds));

        return redisCacheConfiguration;
    }


    /**
     * 获取 JedisFactory
     * @param properties
     * @return
     */
    public static RedisConnectionFactory getJedisConnectionFactory(RedisProperties properties) {
        Objects.requireNonNull(properties);
        Objects.requireNonNull(properties.getHost());
        /**
         * redis 连接设置
         */
        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration(properties.getHost(), properties.getPort());
        standaloneConfiguration.setDatabase(properties.getDatabase());
        if (!properties.getPassword().equals("")) {
            standaloneConfiguration.setPassword(RedisPassword.of(properties.getPassword()));
        }
        Duration timeout = Optional.ofNullable(properties.getTimeout()).orElse(Duration.ofMillis(1000));
        /**
         * redis 连接池设置
         */
        RedisProperties.Pool pool = properties.getJedis().getPool();
        Objects.requireNonNull(pool);
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setMaxTotal(pool.getMaxActive());
        poolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        /**
         * redis service timeout [300s]
         */
        poolConfig.setMinEvictableIdleTimeMillis(280000);
        JedisClientConfiguration clientConfiguration = JedisClientConfiguration.builder()
                .connectTimeout(timeout)
                .readTimeout(timeout)
                .usePooling()
                .poolConfig(poolConfig)
                .build();
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(standaloneConfiguration, clientConfiguration);
        LOGGER.info("JedisConnectionFactory ["+jedisConnectionFactory.toString()+"] be Created , properties "+ properties.toString()+" , PoolConfig "+jedisConnectionFactory.getPoolConfig().toString());
        return jedisConnectionFactory;
    }
}
