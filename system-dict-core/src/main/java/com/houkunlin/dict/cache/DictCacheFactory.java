package com.houkunlin.dict.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.houkunlin.dict.properties.DictProperties;

/**
 * 数据字典缓存工厂接口
 * <p>
 * 定义数据字典缓存对象的构建与回调能力，负责根据配置创建数据字典使用的 {@link Cache} 缓存实例，
 * 并在缓存构建完成后提供回调处理。具体实现由 {@code DictCacheFactoryImpl} 提供。
 * </p>
 *
 * @author HouKunLin
 */
public interface DictCacheFactory {
    /**
     * 构建一个数据字典缓存对象
     *
     * @param name 缓存名称
     * @return 构建完成的缓存对象；当缓存未启用时返回 null
     */
    <K, V> Cache<K, V> build(String name);

    /**
     * 缓存构建完成后的回调处理
     *
     * @param name  缓存名称
     * @param cache 构建完成的缓存对象
     */
    <K, V> void callbackCache(String name, Cache<K, V> cache);

    /**
     * 获取数据字典配置信息对象
     *
     * @return 数据字典配置信息对象
     */
    DictProperties getDictProperties();
}
