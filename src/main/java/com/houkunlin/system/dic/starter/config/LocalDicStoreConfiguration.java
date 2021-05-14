package com.houkunlin.system.dic.starter.config;

import com.houkunlin.system.dic.starter.store.DicStore;
import com.houkunlin.system.dic.starter.store.LocalDicStore;
import com.houkunlin.system.dic.starter.store.RemoteDic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author HouKunLin
 */
@ConditionalOnMissingClass("org.springframework.data.redis.core.RedisTemplate")
@Configuration
public class LocalDicStoreConfiguration {
    /**
     * 当环境中不存在 DicStore Bean 的时候创建一个默认的 DicStore Bean 实例
     *
     * @return DicStore
     */
    @Bean
    @ConditionalOnMissingBean
    public DicStore dicStore(RemoteDic remoteDic) {
        return new LocalDicStore(remoteDic);
    }
}
