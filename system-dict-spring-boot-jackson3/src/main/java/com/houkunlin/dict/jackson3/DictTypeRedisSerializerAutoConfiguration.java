package com.houkunlin.dict.jackson3;

import com.houkunlin.dict.bean.DictType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * DictType 的 Redis 值序列化器自动配置。
 * <p>
 * Redis 依赖是可选的，仅在引入 spring-data-redis 依赖时才注册该序列化器。
 * 独立成类并使用类级 {@link ConditionalOnClass} 保护，避免未引入 Redis 依赖的下游项目在加载配置类时因类缺失而启动失败。
 * 序列化器仅在系统配置启用 Redis 存储（store-type 为 AUTO 或 REDIS）时创建。
 * </p>
 *
 * @author HouKunLin
 */
@ConditionalOnClass({RedisTemplate.class, RedisSerializer.class})
@Configuration(proxyBeanMethods = false)
public class DictTypeRedisSerializerAutoConfiguration {

    /**
     * 注册 DictType 的 Redis 值序列化器。
     * <p>
     * 仅当 store-type 为 AUTO（默认，存在 Redis 时自动使用 Redis 存储）时创建。
     * </p>
     *
     * @return Redis 值序列化器
     */
    @ConditionalOnProperty(prefix = "system.dict", name = "store-type", havingValue = "AUTO", matchIfMissing = true)
    @ConditionalOnMissingBean
    @Bean("dictTypeRedisSerializer")
    public RedisSerializer<DictType> dictTypeRedisSerializer() {
        return createDictTypeRedisSerializer();
    }

    /**
     * 注册 DictType 的 Redis 值序列化器。
     * <p>
     * 仅当 store-type 为 REDIS（显式启用 Redis 存储）时创建。
     * </p>
     *
     * @return Redis 值序列化器
     */
    @ConditionalOnProperty(prefix = "system.dict", name = "store-type", havingValue = "REDIS")
    @ConditionalOnMissingBean
    @Bean("dictTypeRedisSerializer")
    public RedisSerializer<DictType> dictTypeRedisSerializerForRedis() {
        return createDictTypeRedisSerializer();
    }

    /**
     * 创建 DictType 的 Redis 值序列化器。
     *
     * @return Redis 值序列化器
     */
    private RedisSerializer<DictType> createDictTypeRedisSerializer() {
        return new JacksonJsonRedisSerializer<>(DictType.class);
    }
}
