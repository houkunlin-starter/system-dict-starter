package com.houkunlin.system.dict.starter.config;

import com.houkunlin.system.dict.starter.store.DictStore;
import com.houkunlin.system.dict.starter.store.RedisDictStore;
import com.houkunlin.system.dict.starter.store.RemoteDict;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 字典存储配置
 *
 * @author HouKunLin
 */
@ConditionalOnClass(RedisTemplate.class)
@Configuration
public class RedisDictStoreConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public DictStore dicStore(RedisTemplate<Object, Object> redisTemplate, RemoteDict remoteDic) {
        return new RedisDictStore(redisTemplate, remoteDic);
    }
}
