package com.houkunlin.system.dict.starter.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.houkunlin.system.dict.starter.properties.DictPropertiesCache;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 字典缓存工厂配置
 *
 * @author HouKunLin
 * @since 1.4.2
 */
@Configuration
@AllArgsConstructor
public class DictCacheFactory {
    private final DictPropertiesCache propertiesCache;
    private final List<DictCacheCustomizer> cacheCustomizers;

    public Cache<String, String> build() {
        if (!propertiesCache.isEnabled()) {
            return null;
        }
        final Caffeine<Object, Object> builder = Caffeine.newBuilder();
        builder
            .expireAfterWrite(propertiesCache.getDuration())
            .maximumSize(propertiesCache.getMaximumSize())
            .initialCapacity(propertiesCache.getInitialCapacity());

        for (final DictCacheCustomizer customizer : cacheCustomizers) {
            customizer.customize(builder);
        }

        return builder.build();
    }
}
