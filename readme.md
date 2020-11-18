# spring-boot-cache-extend 

> spring-boot-cache-extend  是对spring-cache的包装，提供在spring-boot中使用redis与caffeine缓存开箱即用的功能。

1. 统一的cache key 生成器

> 统一的cache key 有助于我们规范的管理缓存的key，特别是在neibu环境中共用同一redis时，避免缓存key重复:

缓存key命名规范
``` 
    key = cacheName :: application : targetClass . targetMethod : params
```

``` 
    如 : TTL_5::spring-boot-example-cache:TestController.hello:visitor 
```

2. @CacheTarget 注解

> @CacheTarget 是用于定义目标的缓存类，便于在清除缓存时可以随时在别处进行清除

3. 兼容所有的 spring-cache 功能

4. 缓存使用示例

https://github.com/zhengyin/spring-boot-example/blob/master/spring-boot-example-cache/src/test/java/com/izhengyin/springboot/example/cache/test/ApplicationTests.java

## 注意事项

### 1. 使用  @Cacheable(key = "#name") 自定义缓存key时不会使用统一的缓存生成器, 参考示例 customKeyTest

### 2. 使用  @Cacheable(keyGenerator = CacheKeyGeneratorConfig.MY_KEY_GENERATOR ) 自定义缓存key生成器时不会使用统一的缓存生成器, 参考示例 customKeyGeneratorTest

### 3. 由于 Aop 的特性，在类中使用 this 调用方法是不会触发 Aop 增强，因此缓存注解不会生效 . 参考此文章 https://www.ibm.com/developerworks/cn/opensource/os-cn-spring-cache/