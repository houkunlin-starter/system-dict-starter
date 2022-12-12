package com.houkunlin.system.dict.starter.store;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 本地字典存储配置
 *
 * @author HouKunLin
 */
@Configuration(proxyBeanMethods = false)
public class LocalDictStoreConfiguration {
    /**
     * 当环境中不存在 DictStore Bean 的时候创建一个默认的 DictStore Bean 实例
     *
     * @return {@link DictStore}
     */
    @ConditionalOnProperty(prefix = "system.dict", name = "store-type", havingValue = "AUTO", matchIfMissing = true)
    @ConditionalOnMissingClass("org.springframework.data.redis.core.RedisTemplate")
    @Bean
    @ConditionalOnMissingBean
    public DictStore dictStoreAuto(final RemoteDict remoteDict) {
        return new LocalDictStore(remoteDict);
    }

    /**
     * 当环境中不存在 DictStore Bean 的时候创建一个默认的 DictStore Bean 实例
     *
     * @return {@link DictStore}
     */
    @ConditionalOnProperty(prefix = "system.dict", name = "store-type", havingValue = "LOCAL")
    @Bean
    @ConditionalOnMissingBean
    public DictStore dictStoreLocal(final RemoteDict remoteDict) {
        return new LocalDictStore(remoteDict);
    }
}
