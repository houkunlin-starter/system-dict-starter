package com.houkunlin.system.dict.starter;

import com.github.benmanes.caffeine.cache.Cache;
import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
import com.houkunlin.system.dict.starter.cache.DictCacheFactory;
import com.houkunlin.system.dict.starter.store.DictStore;
import org.springframework.stereotype.Component;

/**
 * 系统字典工具
 *
 * @author HouKunLin
 */
@SuppressWarnings("all")
@Component
public class DictUtil {
    public static final String TYPE_PREFIX = "dict:t:";
    public static final String VALUE_PREFIX = "dict:v:";

    private static DictStore store;
    /**
     * 字典值缓存
     */
    private static Cache<String, String> cache;

    public DictUtil(final DictStore store, final DictCacheFactory cacheFactory) {
        DictUtil.store = store;
        cache = cacheFactory.build();
    }

    public static DictTypeVo getDictType(String type) {
        if (type == null || store == null) {
            return null;
        }
        return store.getDictType(type);
    }

    public static String getDictText(String type, String value) {
        if (type == null || value == null || store == null) {
            return null;
        }
        if (cache == null) {
            return store.getDictText(type, value);
        }
        return cache.get(dictKey(type, value), o -> store.getDictText(type, value));
    }

    public static String dictKey(String type) {
        return TYPE_PREFIX + type;
    }

    public static String dictKey(DictValueVo value) {
        return VALUE_PREFIX + value.getDictType() + ":" + value.getValue();
    }

    public static String dictKey(String type, Object value) {
        return VALUE_PREFIX + type + ":" + value;
    }
}
