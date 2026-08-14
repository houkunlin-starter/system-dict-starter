package com.houkunlin.dict.store;

import com.houkunlin.dict.bean.DictType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis 字典存储配置类
 * <p>
 * 该类负责配置 Redis 字典存储的相关 Bean。当系统中存在 Redis 环境时，
 * 可以使用 Redis 存储实现 {@link RedisDictStore}。
 * 该配置类提供了自动配置和手动指定配置两种方式。
 * </p>
 *
 * @author HouKunLin
 * @since 1.0.0
 */
@ConditionalOnClass(RedisTemplate.class)
@Configuration(proxyBeanMethods = false)
public class RedisDictStoreConfiguration {

    /**
     * 自动配置 Redis 字典存储 Bean
     * <p>
     * 当系统配置为自动模式（AUTO）时，创建一个默认的 Redis 字典存储 Bean 实例。
     * 该方法使用了条件注解来确保在合适的场景下才创建此 Bean：
     * <ul>
     * <li>当 store-type 配置为 AUTO 或未配置时</li>
     * <li>当容器中不存在其他 DictStore Bean 时</li>
     * </ul>
     * 该方法依赖于 RedisTemplate 和 RemoteDict Bean 的存在。
     * </p>
     *
     * @param redisTemplate1 Redis 模板对象，用于操作 Redis 数据
     * @param remoteDict 远程字典获取接口，用于当 Redis 中不存在字典数据时尝试远程获取
     * @return Redis 字典存储实例
     */
    @ConditionalOnProperty(prefix = "system.dict", name = "store-type", havingValue = "AUTO", matchIfMissing = true)
    @Bean
    @ConditionalOnMissingBean
    public DictStore dictStoreAuto(final RedisTemplate<String, DictType> redisTemplate1, final RemoteDict remoteDict) {
        return new RedisDictStore(redisTemplate1, remoteDict);
    }

    /**
     * 手动配置 Redis 字典存储 Bean
     * <p>
     * 当系统配置为 Redis 模式（REDIS）时，创建一个默认的 Redis 字典存储 Bean 实例。
     * 该方法使用了条件注解来确保在合适的场景下才创建此 Bean：
     * <ul>
     * <li>当 store-type 配置为 REDIS 时</li>
     * <li>当容器中不存在其他 DictStore Bean 时</li>
     * </ul>
     * 该方法依赖于 RedisTemplate 和 RemoteDict Bean 的存在。
     * </p>
     *
     * @param redisTemplate1 Redis 模板对象，用于操作 Redis 数据
     * @param remoteDict 远程字典获取接口，用于当 Redis 中不存在字典数据时尝试远程获取
     * @return Redis 字典存储实例
     */
    @ConditionalOnProperty(prefix = "system.dict", name = "store-type", havingValue = "REDIS")
    @Bean
    @ConditionalOnMissingBean
    public DictStore dictStoreRedis(final RedisTemplate<String, DictType> redisTemplate1, final RemoteDict remoteDict) {
        return new RedisDictStore(redisTemplate1, remoteDict);
    }
}
