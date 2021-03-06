package com.houkunlin.system.dict.starter.store;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 本地字典存储配置
 *
 * @author HouKunLin
 */
@ConditionalOnMissingClass("org.springframework.data.redis.core.RedisTemplate")
@Configuration(proxyBeanMethods = false)
public class LocalDictStoreConfiguration {
    /**
     * 当环境中不存在 DictStore Bean 的时候创建一个默认的 DictStore Bean 实例
     *
     * @return {@link DictStore}
     */
    @Bean
    @ConditionalOnMissingBean
    public DictStore dictStore(RemoteDict remoteDict) {
        return new LocalDictStore(remoteDict);
    }
}
