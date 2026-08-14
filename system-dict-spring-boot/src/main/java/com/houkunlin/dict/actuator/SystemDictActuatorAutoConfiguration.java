package com.houkunlin.dict.actuator;

import com.houkunlin.dict.provider.DictProvider;
import com.houkunlin.dict.provider.SystemDictProvider;
import com.houkunlin.dict.store.DictStore;
import com.houkunlin.dict.store.RemoteDict;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 系统字典监控端点配置类，用于自动配置字典相关的监控端点。
 *
 * @author HouKunLin
 * @since 1.3.0
 */
@ConditionalOnClass(Endpoint.class)
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class SystemDictActuatorAutoConfiguration {

    /**
     * 配置字典端点。
     *
     * @param providers  字典提供者列表
     * @param store      字典存储
     * @param remoteDict 远程字典
     * @return 字典端点
     */
    @ConditionalOnBean({DictStore.class, RemoteDict.class})
    @Bean
    public DictEndpoint dictEndpoint(List<DictProvider> providers, DictStore store, RemoteDict remoteDict) {
        return new DictEndpoint(providers, store, remoteDict);
    }

    /**
     * 配置字典刷新端点。
     *
     * @param applicationEventPublisher 应用事件发布器
     * @return 字典刷新端点
     */
    @Bean
    public RefreshDictEndpoint refreshDictEndpoint(final ApplicationEventPublisher applicationEventPublisher) {
        return new RefreshDictEndpoint(applicationEventPublisher);
    }

    /**
     * 配置系统字典提供者端点。
     *
     * @param systemDictProvider 系统字典提供者
     * @return 系统字典提供者端点
     */
    @ConditionalOnBean(SystemDictProvider.class)
    @Bean
    public SystemDictProviderEndpoint systemDictProviderEndpoint(final SystemDictProvider systemDictProvider) {
        return new SystemDictProviderEndpoint(systemDictProvider);
    }
}
