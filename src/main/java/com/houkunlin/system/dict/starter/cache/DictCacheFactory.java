package com.houkunlin.system.dict.starter.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.houkunlin.system.dict.starter.properties.DictProperties;
import com.houkunlin.system.dict.starter.properties.DictPropertiesCache;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 字典缓存工厂配置
 *
 * @author HouKunLin
 * @since 1.4.2
 */
@Slf4j
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
     * 环境变量
     */
    private final Environment environment;

    /**
     * 构建缓存对象
     *
     * @param <K> KEY
     * @param <V> VALUE
     * @return 缓存对象
     * @deprecated 已过时
     */
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
        final Caffeine<Object, Object> builder;
        if (propertiesCache.isUseCaffeineSpec()) {
            builder = Caffeine.from(getCaffeineSpec(propertiesCache.getCaffeine()));
        } else {
            boolean b1 = environment.containsProperty("system.dict.cache.maximum-size");
            boolean b2 = environment.containsProperty("system.dict.cache.initial-capacity");
            boolean b3 = environment.containsProperty("system.dict.cache.duration");
            if (b1 || b2 || b3) {
                builder = Caffeine.newBuilder();
                builder
                    .expireAfterWrite(propertiesCache.getDuration())
                    .maximumSize(propertiesCache.getMaximumSize())
                    .initialCapacity(propertiesCache.getInitialCapacity());
                if (log.isWarnEnabled()) {
                    log.warn("建议使用 system.dict.cache.caffeine.spec 设置缓存参数配置");
                }
                if (b1 && log.isWarnEnabled()) {
                    log.warn("配置参数 system.dict.cache.maximum-size 已经过时，请使用 system.dict.cache.caffeine.spec");
                }
                if (b2 && log.isWarnEnabled()) {
                    log.warn("配置参数 system.dict.cache.initial-capacity 已经过时，请使用 system.dict.cache.caffeine.spec");
                }
                if (b3 && log.isWarnEnabled()) {
                    log.warn("配置参数 system.dict.cache.duration 已经过时，请使用 system.dict.cache.caffeine.spec");
                }
            } else {
                builder = Caffeine.from(getCaffeineSpec(propertiesCache.getCaffeine()));
            }
        }

        for (final DictCacheCustomizer customizer : cacheCustomizers) {
            customizer.customize(name, builder);
        }

        Cache<K, V> cache = builder.build();

        callbackCache(name, cache);

        return cache;
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

    /**
     * 获取 CaffeineSpec
     *
     * @param caffeineProperties caffeineProperties
     * @return CaffeineSpec
     */
    private String getCaffeineSpec(DictPropertiesCache.Caffeine caffeineProperties) {
        if (caffeineProperties == null) {
            return DictPropertiesCache.DEFAULT_CAFFEINE_SPEC;
        }
        String caffeineSpec = caffeineProperties.getSpec();
        if (StringUtils.hasText(caffeineSpec)) {
            return caffeineSpec;
        }
        return DictPropertiesCache.DEFAULT_CAFFEINE_SPEC;
    }

}
