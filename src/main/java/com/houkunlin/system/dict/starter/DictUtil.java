package com.houkunlin.system.dict.starter;

import com.github.benmanes.caffeine.cache.Cache;
import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
import com.houkunlin.system.dict.starter.cache.DictCacheFactory;
import com.houkunlin.system.dict.starter.properties.DictPropertiesStorePrefixKey;
import com.houkunlin.system.dict.starter.store.DictStore;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 系统字典工具
 *
 * @author HouKunLin
 */
@SuppressWarnings("all")
@Component
public class DictUtil {
    public static String TYPE_PREFIX = "dict:t:";
    public static String VALUE_PREFIX = "dict:v:";
    public static String PARENT_PREFIX = "dict:p:";

    private static DictStore store;
    /**
     * 字典值缓存
     */
    private static Cache<String, String> cache;
    private static Cache<String, AtomicInteger> missCache;
    private static int missNum = Integer.MAX_VALUE;

    public DictUtil(final DictStore store, final DictCacheFactory cacheFactory) {
        DictUtil.store = store;
        cache = cacheFactory.build();
        missCache = cacheFactory.build();
        missNum = cacheFactory.getDictProperties().getCache().getMissNum();
    }

    /**
     * 初始化 缓存键 前缀信息
     *
     * @param properties 配置信息
     * @since 1.4.7
     */
    public static void initPrefix(final DictPropertiesStorePrefixKey properties) {
        TYPE_PREFIX = properties.getTypePrefix();
        VALUE_PREFIX = properties.getValuePrefix();
        PARENT_PREFIX = properties.getParentPrefix();
    }

    public static DictTypeVo getDictType(String type) {
        if (type == null || store == null) {
            return null;
        }
        return store.getDictType(type);
    }

    /**
     * 获取字典文本
     *
     * @param type  字典类型
     * @param value 字典值
     * @return
     */
    public static String getDictText(String type, String value) {
        if (type == null || value == null || store == null) {
            return null;
        }
        if (cache == null || missCache == null) {
            return store.getDictText(type, value);
        }
        final String dictKey = dictKey(type, value);
        final String result = cache.getIfPresent(dictKey);
        if (result != null) {
            return result;
        }
        final AtomicInteger integer = missCache.get(dictKey, s -> new AtomicInteger(1));
        if (integer.get() > missNum) {
            return null;
        }

        final String dictText = store.getDictText(type, value);
        if (dictText == null) {
            // 未命中数据
            integer.incrementAndGet();
        } else {
            cache.put(dictKey, dictText);
        }
        return dictText;
    }

    /**
     * 获取字典父级值
     *
     * @param type  字典类型
     * @param value 字典值
     * @return 字典父级值
     * @since 1.4.6
     */
    public static String getDictParentValue(String type, String value) {
        if (type == null || value == null || store == null) {
            return null;
        }
        if (cache == null || missCache == null) {
            return store.getDictParentValue(type, value);
        }
        final String dictParentKey = dictParentKey(type, value);
        final String result = cache.getIfPresent(dictParentKey);
        if (result != null) {
            return result;
        }
        final AtomicInteger integer = missCache.get(dictParentKey, s -> new AtomicInteger(1));
        if (integer.get() > missNum) {
            return null;
        }

        final String parentValue = store.getDictParentValue(type, value);
        if (parentValue == null) {
            // 未命中数据
            integer.incrementAndGet();
        } else {
            cache.put(dictParentKey, parentValue);
        }
        return parentValue;
    }

    public static String dictKey(String type) {
        return TYPE_PREFIX + type;
    }

    public static String dictKey(DictValueVo value) {
        return VALUE_PREFIX + value.getDictType() + ":" + value.getValue();
    }

    /**
     * 构建字典父级值缓存 KEY
     *
     * @param value 字典值对象
     * @return 字典父级值缓存 KEY
     * @since 1.4.6
     */
    public static String dictParentKey(DictValueVo value) {
        return PARENT_PREFIX + value.getDictType() + ":" + value.getValue();
    }

    public static String dictKey(String type, Object value) {
        return VALUE_PREFIX + type + ":" + value;
    }

    /**
     * 构建字典父级值缓存 KEY
     *
     * @param type  字典类型
     * @param value 字典值
     * @return 字典父级值缓存 KEY
     * @since 1.4.6
     */
    public static String dictParentKey(String type, Object value) {
        return PARENT_PREFIX + type + ":" + value;
    }
}
