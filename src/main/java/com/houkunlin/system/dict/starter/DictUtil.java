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

    public static DictTypeVo getDicType(String type) {
        if (type == null || store == null) {
            return null;
        }
        return store.getDicType(type);
    }

    public static String getDicValueTitle(String type, String value) {
        if (type == null || value == null || store == null) {
            return null;
        }
        return cache.get(dicKey(type, value), o -> store.getDicValueTitle(type, value));
    }

    public static String dicKey(String type) {
        return "dic:" + type;
    }

    public static String dicKey(DictValueVo value) {
        return "dic:" + value.getDicType() + ":" + value.getValue();
    }

    public static String dicKey(String type, Object value) {
        return "dic:" + type + ":" + value;
    }
}
