package com.houkunlin.system.dict.starter;

import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置（在存在 Redis 环境时自动配置相关对象，无论后续是否需要）
 *
 * @author HouKunLin
 */
@ConditionalOnClass(RedisTemplate.class)
@Configuration
@AllArgsConstructor
public class DictRedisConfiguration {
    /**
     * 创建一个默认的 DictTypeVo 类型 Redis 客户端
     *
     * @param connectionFactory RedisConnectionFactory
     * @return RedisTemplate<String, DictTypeVo>
     */
    @ConditionalOnMissingBean
    @Bean
    public RedisTemplate<String, DictTypeVo> dictTypeRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, DictTypeVo> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(DictTypeVo.class));
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }

    /**
     * 创建一个默认的 Redis 消息监听容器（在后续可用在使用 Redis 发布/订阅 功能进行字典刷新通知）
     *
     * @param connectionFactory RedisConnectionFactory
     * @return RedisMessageListenerContainer
     */
    @ConditionalOnMissingBean
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
}
