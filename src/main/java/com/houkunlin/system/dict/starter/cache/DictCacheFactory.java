package com.houkunlin.system.dict.starter.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.houkunlin.system.dict.starter.properties.DictProperties;
import com.houkunlin.system.dict.starter.properties.DictPropertiesCache;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 字典缓存工厂配置
 *
 * @author HouKunLin
 * @since 1.4.2
 */
@Getter
@Configuration(proxyBeanMethods = false)
@AllArgsConstructor
public class DictCacheFactory {
    private final DictProperties dictProperties;
    private final List<DictCacheCustomizer> cacheCustomizers;

    @Deprecated
    public <K, V> Cache<K, V> build() {
        return build(null);
    }

    /**
     * 构建缓存对象
     *
     * @param name 缓存名称
     * @param <K>  KEY
     * @param <V>  VALUE
     * @return 缓存对象
     * @since 1.5.5
     */
    public <K, V> Cache<K, V> build(String name) {
        final DictPropertiesCache propertiesCache = dictProperties.getCache();
        if (!propertiesCache.isEnabled()) {
            return null;
        }
        final Caffeine<Object, Object> builder = Caffeine.newBuilder();
        builder
            .expireAfterWrite(propertiesCache.getDuration())
            .maximumSize(propertiesCache.getMaximumSize())
            .initialCapacity(propertiesCache.getInitialCapacity());

        for (final DictCacheCustomizer customizer : cacheCustomizers) {
            customizer.customize(name, builder);
        }

        return builder.build();
    }

    /**
     * 回调构建成功的缓存对象
     *
     * @param name  缓存名称
     * @param cache 缓存对象
     * @param <K>   KEY
     * @param <V>   VALUE
     * @since 1.5.5
     */
    public <K, V> void callbackCache(String name, Cache<K, V> cache) {
        for (final DictCacheCustomizer customizer : cacheCustomizers) {
            customizer.callbackCache(name, cache);
        }
    }
}
