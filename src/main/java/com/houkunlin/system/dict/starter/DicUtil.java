package com.houkunlin.system.dict.starter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.houkunlin.system.dict.starter.bean.DicTypeVo;
import com.houkunlin.system.dict.starter.bean.DicValueVo;
import com.houkunlin.system.dict.starter.store.DicStore;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 系统字典工具
 *
 * @author HouKunLin
 */
@SuppressWarnings("all")
@Component
public class DicUtil {

    private static DicStore store;
    /**
     * 字典值缓存
     */
    private static Cache<String, String> cache;

    public DicUtil(final DicStore store) {
        DicUtil.store = store;
        cache = Caffeine
                .newBuilder()
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .maximumSize(500)
                .initialCapacity(50)
                .build();
    }

    public static DicTypeVo getDicType(String type) {
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

    public static String dicKey(DicValueVo value) {
        return "dic:" + value.getDicType() + ":" + value.getValue();
    }

    public static String dicKey(String type, Object value) {
        return "dic:" + type + ":" + value;
    }
}
