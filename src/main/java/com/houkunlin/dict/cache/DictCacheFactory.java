package com.houkunlin.dict.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.houkunlin.dict.properties.DictProperties;
import com.houkunlin.dict.properties.DictPropertiesCache;
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
    /**
     * 数据字典配置信息
     *
     * @return 数据字典配置信息
     */
    private final DictProperties dictProperties;
    /**
     * 数据字典自定义缓存定制器
     *
     * @return 数据字典自定义缓存定制器
     */
    private final List<DictCacheCustomizer> cacheCustomizers;

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
