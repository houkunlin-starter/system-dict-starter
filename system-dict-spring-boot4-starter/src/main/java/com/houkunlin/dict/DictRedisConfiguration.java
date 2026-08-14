package com.houkunlin.dict;

import com.houkunlin.dict.bean.DictType;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Redis 配置（在存在 Redis 环境时自动配置相关对象，无论后续是否需要）
 *
 * @author HouKunLin
 */
@ConditionalOnClass(RedisTemplate.class)
@Configuration(proxyBeanMethods = false)
@AllArgsConstructor
public class DictRedisConfiguration {
    /**
     * 系统字典 RedisTemplate Bean 名称
     */
    public static final String DICT_REDIS_BEAN_NAME = "DictTypeRedisTemplate";

    /**
     * 创建一个默认的 DictType 类型 Redis 客户端
     *
     * @param connectionFactory RedisConnectionFactory
     * @return RedisTemplate&lt;String, DictType&gt;
     */
    @ConditionalOnMissingBean(name = DICT_REDIS_BEAN_NAME)
    @Bean(DICT_REDIS_BEAN_NAME)
    public RedisTemplate<String, DictType> dictTypeRedisTemplate(final RedisConnectionFactory connectionFactory) {
        final RedisTemplate<String, DictType> redisTemplate = new RedisTemplate<>();
        redisTemplate.setBeanClassLoader(Thread.currentThread().getContextClassLoader());
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(new JacksonJsonRedisSerializer<>(DictType.class));
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(RedisSerializer.string());
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 创建一个默认的 Redis 消息监听容器（在后续可用在使用 Redis 发布/订阅 功能进行字典刷新通知）
     *
     * @param connectionFactory RedisConnectionFactory
     * @return RedisMessageListenerContainer
     */
    @ConditionalOnProperty(prefix = "system.dict", name = "mq-type", havingValue = "REDIS")
    @ConditionalOnMissingBean
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(final RedisConnectionFactory connectionFactory) {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
}
