package com.houkunlin.dict.store;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 本地字典存储配置类
 * <p>
 * 该类负责配置本地字典存储的相关 Bean。当系统中不存在 Redis 环境时，
 * 默认使用本地 Map 存储实现 {@link LocalDictStore}。
 * 该配置类提供了自动配置和手动指定配置两种方式。
 * </p>
 *
 * @author HouKunLin
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class LocalDictStoreConfiguration {
    /**
     * 自动配置本地字典存储 Bean
     * <p>
     * 当系统配置为自动模式（AUTO）且不存在 Redis 环境时，创建一个默认的本地字典存储 Bean 实例。
     * 该方法使用了多个条件注解来确保在合适的场景下才创建此 Bean：
     * <ul>
     * <li>当 store-type 配置为 AUTO 或未配置时</li>
     * <li>当系统中不存在 RedisTemplate 类时</li>
     * <li>当容器中不存在其他 DictStore Bean 时</li>
     * </ul>
     * </p>
     *
     * @param remoteDict 远程字典获取接口，用于当本地缓存中不存在字典数据时尝试远程获取
     * @return 本地字典存储实例
     */
    @ConditionalOnProperty(prefix = "system.dict", name = "store-type", havingValue = "AUTO", matchIfMissing = true)
    @ConditionalOnMissingClass("org.springframework.data.redis.core.RedisTemplate")
    @Bean
    @ConditionalOnMissingBean
    public DictStore dictStoreAuto(final RemoteDict remoteDict) {
        return new LocalDictStore(remoteDict);
    }

    /**
     * 手动配置本地字典存储 Bean
     * <p>
     * 当系统配置为本地模式（LOCAL）时，创建一个默认的本地字典存储 Bean 实例。
     * 该方法使用了条件注解来确保在合适的场景下才创建此 Bean：
     * <ul>
     * <li>当 store-type 配置为 LOCAL 时</li>
     * <li>当容器中不存在其他 DictStore Bean 时</li>
     * </ul>
     * </p>
     *
     * @param remoteDict 远程字典获取接口，用于当本地缓存中不存在字典数据时尝试远程获取
     * @return 本地字典存储实例
     */
    @ConditionalOnProperty(prefix = "system.dict", name = "store-type", havingValue = "LOCAL")
    @Bean
    @ConditionalOnMissingBean
    public DictStore dictStoreLocal(final RemoteDict remoteDict) {
        return new LocalDictStore(remoteDict);
    }
}
