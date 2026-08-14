package com.houkunlin.dict.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.houkunlin.dict.properties.DictProperties;

public interface IDictCacheFactory {
    <K, V> Cache<K, V> build(String name);

    <K, V> void callbackCache(String name, Cache<K, V> cache);

    DictProperties getDictProperties();
}
