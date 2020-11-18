package com.izhengyin.springboot.cache;
import com.izhengyin.springboot.cache.constant.CacheDrive;
import com.izhengyin.springboot.cache.constant.GeneratorName;
import com.izhengyin.springboot.cache.constant.RedisInstance;
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
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author zhengyin  zhengyinit@outlook.com
 * @date Created on 2019-11-05 15:34
 */
@Configuration
public class CacheStarterEnableAutoConfiguration {

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
        return RedisConfig.getJedisConnectionFactory(properties);
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
        return new RedisCacheManager(cacheWriter, RedisConfig.getRedisCacheConfigurationWithTtl(1), RedisConfig.getRedisCacheConfigurationMap());
    }

    @Primary
    @Qualifier(CacheDrive.CAFFEINE)
    @Bean(name = CacheDrive.CAFFEINE)
    public CacheManager caffeineCacheManager(){
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(CaffeineConfig.getCacheNameConfigs());
        return manager;
    }

    @Bean(GeneratorName.DEFAULT)
    public KeyGenerator keyGenerator(CachingConfigurerSupport cachingConfigurerSupport){
        return cachingConfigurerSupport.keyGenerator();
    }
}
