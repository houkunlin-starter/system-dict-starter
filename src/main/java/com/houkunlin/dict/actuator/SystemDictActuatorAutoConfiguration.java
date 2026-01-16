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
 * 系统字典 监控端点配置
 *
 * @author HouKunLin
 */
@ConditionalOnClass(Endpoint.class)
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class SystemDictActuatorAutoConfiguration {

    @ConditionalOnBean({DictStore.class, RemoteDict.class})
    @Bean
    public DictEndpoint dictEndpoint(List<DictProvider> providers, DictStore store, RemoteDict remoteDict) {
        return new DictEndpoint(providers, store, remoteDict);
    }

    @Bean
    public RefreshDictEndpoint refreshDictEndpoint(final ApplicationEventPublisher applicationEventPublisher) {
        return new RefreshDictEndpoint(applicationEventPublisher);
    }

    @ConditionalOnBean(SystemDictProvider.class)
    @Bean
    public SystemDictProviderEndpoint systemDictProviderEndpoint(final SystemDictProvider systemDictProvider) {
        return new SystemDictProviderEndpoint(systemDictProvider);
    }
}
