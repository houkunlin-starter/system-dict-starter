package com.houkunlin.system.dict.starter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
import com.houkunlin.system.dict.starter.store.DictStore;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 系统字典工具
 *
 * @author HouKunLin
 */
@SuppressWarnings("all")
@Component
public class DictUtil {

    private static DictStore store;
    /**
     * 字典值缓存
     */
    private static Cache<String, String> cache;

    public DictUtil(final DictStore store) {
        DictUtil.store = store;
        cache = Caffeine
                .newBuilder()
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .maximumSize(500)
                .initialCapacity(50)
                .build();
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
        return cache.get(dictKey(type, value), o -> store.getDictText(type, value));
    }

    public static String dictKey(String type) {
        return "dic:" + type;
    }

    public static String dictKey(DictValueVo value) {
        return "dic:" + value.getDictType() + ":" + value.getValue();
    }

    public static String dictKey(String type, Object value) {
        return "dic:" + type + ":" + value;
    }
}
