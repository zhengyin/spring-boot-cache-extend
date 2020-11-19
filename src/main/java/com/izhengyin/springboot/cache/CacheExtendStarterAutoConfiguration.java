package com.izhengyin.springboot.cache;
import com.izhengyin.springboot.cache.constant.CacheDrive;
import com.izhengyin.springboot.cache.constant.GeneratorName;
import com.izhengyin.springboot.cache.constant.RedisInstance;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhengyin  zhengyinit@outlook.com
 * @date Created on 2019-11-05 15:34
 */
@Configuration
public class CacheExtendStarterAutoConfiguration {

    private final static Logger LOGGER = LoggerFactory.getLogger(CacheExtendStarterAutoConfiguration.class);

    @Value("${spring.application.name}")
    private String application;

    /**
     * create AbstractCachingConfigurer
     * @return
     */
    @Bean
    public CachingConfigurerSupport cachingConfigurerSupport(){
        return new CachingConfigurer(application);
    }

    /**
     * redis cache properties
     * @return
     */
    @Primary
    @Qualifier(RedisInstance.REDIS_CACHE_PROPERTIES)
    @Bean(name = RedisInstance.REDIS_CACHE_PROPERTIES)
    @ConfigurationProperties(prefix = "redis.cache")
    public RedisProperties redisCacheProperties() {
        return new RedisProperties();
    }

    /**
     * create redis template
     * @param jedisConnectionFactory
     * @return
     */
    @Qualifier(RedisInstance.REDIS_CACHE_TEMPLATE)
    @Bean(name = RedisInstance.REDIS_CACHE_TEMPLATE)
    public StringRedisTemplate redisCacheTemplate(@Qualifier(RedisInstance.REDIS_CACHE_CONNECTION_FACTORY) RedisConnectionFactory jedisConnectionFactory) {
        return new StringRedisTemplate(jedisConnectionFactory);
    }

    /**
     * create redis connection factory
     * @param properties
     * @return
     */
    @Primary
    @Qualifier(RedisInstance.REDIS_CACHE_CONNECTION_FACTORY)
    @Bean(name = RedisInstance.REDIS_CACHE_CONNECTION_FACTORY)
    public RedisConnectionFactory redisCacheConnectionFactory(@Qualifier(RedisInstance.REDIS_CACHE_PROPERTIES) RedisProperties properties) {
        return getJedisConnectionFactory(properties);
    }

    /**
     * create redis cache cacheManager
     * @param jedisConnectionFactory
     * @return
     */
    @Qualifier(CacheDrive.REDIS)
    @Bean(name = CacheDrive.REDIS)
    public CacheManager cacheManager(@Qualifier(RedisInstance.REDIS_CACHE_CONNECTION_FACTORY) RedisConnectionFactory jedisConnectionFactory) {
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(jedisConnectionFactory);
        return new RedisCacheManager(cacheWriter, RedisCacheTtlConfig.getRedisCacheConfigurationWithTtl(1), RedisCacheTtlConfig.getRedisCacheConfigurationMap());
    }

    @Primary
    @Qualifier(CacheDrive.CAFFEINE)
    @Bean(name = CacheDrive.CAFFEINE)
    public CacheManager caffeineCacheManager(){
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(CaffeineCacheTtlConfig.getCacheNameConfigs());
        return manager;
    }

    @Bean(GeneratorName.DEFAULT)
    public KeyGenerator keyGenerator(CachingConfigurerSupport cachingConfigurerSupport){
        return cachingConfigurerSupport.keyGenerator();
    }

    /**
     * 获取 JedisFactory
     * @param properties
     * @return
     */
    public static RedisConnectionFactory getJedisConnectionFactory(RedisProperties properties) {
        Objects.requireNonNull(properties);
        Objects.requireNonNull(properties.getHost());
        //redis 连接设置
        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration(properties.getHost(), properties.getPort());
        standaloneConfiguration.setDatabase(properties.getDatabase());
        if (!properties.getPassword().equals("")) {
            standaloneConfiguration.setPassword(RedisPassword.of(properties.getPassword()));
        }
        Duration timeout = Optional.ofNullable(properties.getTimeout()).orElse(Duration.ofMillis(1000));
        //redis 连接池设置
        RedisProperties.Pool pool = properties.getJedis().getPool();
        Objects.requireNonNull(pool);
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setMaxTotal(pool.getMaxActive());
        poolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        //redis service timeout [300s]
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
