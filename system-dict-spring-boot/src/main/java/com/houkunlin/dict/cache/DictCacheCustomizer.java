package com.houkunlin.dict.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * 字典缓存对象构造处理器接口，用于自定义字典缓存的构建过程。
 *
 * @author HouKunLin
 * @since 1.4.2
 */
public interface DictCacheCustomizer {
    /**
     * 处理缓存对象构造。
     *
     * @param caffeine 缓存对象构造器
     */
    default void customize(final Caffeine<Object, Object> caffeine) {
    }

    /**
     * 处理缓存对象构造（带缓存名称）。
     *
     * @param name     缓存名称
     * @param caffeine 缓存对象构造器
     * @since 1.5.5
     */
    default void customize(String name, final Caffeine<Object, Object> caffeine) {
        customize(caffeine);
    }

    /**
     * 回调构建成功的缓存对象。
     * 可在此方法中把缓存加入到 {@link org.springframework.cache.caffeine.CaffeineCacheManager#registerCustomCache(String, Cache)} 进行统一管理
     *
     * @param name  缓存名称
     * @param cache 缓存对象
     * @param <K>   KEY 类型
     * @param <V>   VALUE 类型
     * @since 1.5.5
     */
    default <K, V> void callbackCache(String name, Cache<K, V> cache) {
    }
}
