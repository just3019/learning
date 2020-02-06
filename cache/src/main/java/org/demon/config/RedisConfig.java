package org.demon.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;

/**
 * @author demon
 * @version 1.0.0
 */
@Configuration
//开启spring cache注解功能
@EnableCaching
@Slf4j
public class RedisConfig {

//    /**
//     * 单机配置
//     */
//    @Bean
//    @ConditionalOnProperty(name = "mode", havingValue = "single")
//    public LettuceConnectionFactory redisConnectionFactory() {
//        log.info("单机配置");
//        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("127.0.0.1", 6379));
//    }

//    /**
//     * 主从配置
//     */
//    @Bean
//    public LettuceConnectionFactory redisConnectionFactory() {
//        System.out.println("使用读写分离版本");
//        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
//                .readFrom(ReadFrom.REPLICA_PREFERRED)
//                .build();
//        // 此处
//        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration("127.0.0.1", 6379);
//        return new LettuceConnectionFactory(serverConfig, clientConfig);
//    }

//    @Bean
//    public LettuceConnectionFactory redisConnectionFactory(){
//        System.out.println("使用哨兵机制");
//        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
//                .master("mymaster")
//                .sentinel("127.0.0.1", 26379)
//                .sentinel("127.0.0.1", 26380)
//                .sentinel("127.0.0.1", 26381);
//        return new LettuceConnectionFactory(sentinelConfig);
//    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        System.out.println("使用cluster模式");
        return new LettuceConnectionFactory(
                new RedisClusterConfiguration(Arrays.asList(
                        "127.0.0.1:6381",
                        "127.0.0.1:6382",
                        "127.0.0.1:6383",
                        "127.0.0.1:6384",
                        "127.0.0.1:6385",
                        "127.0.0.1:6386"
                )));
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);

    }

}
