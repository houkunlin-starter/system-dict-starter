package com.houkunlin.system.dict.starter.store;

import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 字典存储配置
 *
 * @author HouKunLin
 */
@ConditionalOnClass(RedisTemplate.class)
@Configuration
@AllArgsConstructor
public class RedisDictStoreConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public RedisTemplate<String, DictTypeVo> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, DictTypeVo> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(DictTypeVo.class));
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public DictStore dictStore(RedisTemplate<String, DictTypeVo> dictTypeRedisTemplate, RedisTemplate<String, String> dictValueRedisTemplate, RemoteDict remoteDict) {
        return new RedisDictStore(dictTypeRedisTemplate, dictValueRedisTemplate, remoteDict);
    }
}
