package com.houkunlin.dict.store;

import com.houkunlin.dict.DictUtil;
import com.houkunlin.dict.bean.DictType;
import com.houkunlin.dict.bean.DictValue;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用本地 Map 来存储系统字典信息。当不存在 Redis 环境时默认使用该存储方式。
 *
 * @author HouKunLin
 */
@AllArgsConstructor
public class LocalDictStore implements DictStore, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(LocalDictStore.class);
    private static final ConcurrentHashMap<String, DictType> CACHE_TYPE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, DictType> CACHE_SYSTEM_TYPE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> CACHE_TEXT = new ConcurrentHashMap<>();
    private final RemoteDict remoteDict;

    @Override
    public void store(final DictType dictType) {
        final List<DictValue> children = dictType.getChildren();
        if (children == null) {
            removeDictType(dictType.getType());
        } else {
            CACHE_TYPE.put(dictType.getType(), dictType);
        }
    }

    @Override
    public void storeSystemDict(DictType dictType) {
        final List<DictValue> children = dictType.getChildren();
        if (children == null) {
            CACHE_SYSTEM_TYPE.remove(dictType.getType());
        } else {
            CACHE_SYSTEM_TYPE.put(dictType.getType(), dictType);
        }
    }

    @Override
    public void store(final Iterator<DictValue> iterator) {
        iterator.forEachRemaining(valueVo -> {
            final String dictKey = DictUtil.dictKey(valueVo);
            final String title = valueVo.getTitle();
            if (title == null) {
                CACHE_TEXT.remove(dictKey);
                if (logger.isDebugEnabled()) {
                    logger.debug("[removeDictValue] 字典值文本被删除 {}", dictKey);
                }
            } else {
                CACHE_TEXT.put(dictKey, title);
                // @since 1.4.6 - START
                final String dictParentKey = DictUtil.dictParentKey(valueVo);
                final Object parentValue = valueVo.getParentValue();
                if (parentValue == null) {
                    CACHE_TEXT.remove(dictParentKey);
                } else {
                    CACHE_TEXT.put(dictParentKey, parentValue.toString());
                }
                // @since 1.4.6 - END
            }
        });
    }

    @Override
    public void removeDictType(final String dictType) {
        CACHE_TYPE.remove(dictType);
        if (logger.isDebugEnabled()) {
            logger.debug("[removeDictType] 字典类型被删除 {}", dictType);
        }
        final String prefix = DictUtil.VALUE_PREFIX.concat(dictType);
        CACHE_TEXT.entrySet().removeIf(entry -> {
            final String entryKey = entry.getKey();
            if (entryKey != null && entryKey.startsWith(prefix)) {
                logger.debug("[removeDictType] 字典值文本被删除 {}", entryKey);
                return true;
            }
            return false;
        });
    }

    @Override
    public Set<String> dictTypeKeys() {
        return CACHE_TYPE.keySet();
    }

    @Override
    public Set<String> systemDictTypeKeys() {
        return CACHE_SYSTEM_TYPE.keySet();
    }

    @Override
    public DictType getDictType(final String type) {
        final DictType typeVo = CACHE_TYPE.get(type);
        if (typeVo != null) {
            return typeVo;
        }
        return remoteDict.getDictType(type);
    }

    @Override
    public String getDictText(final String type, final String value) {
        final String title = CACHE_TEXT.get(DictUtil.dictKey(type, value));
        if (title != null) {
            return title;
        }
        return remoteDict.getDictText(type, value);
    }

    @Override
    public String getDictParentValue(final String type, final String value) {
        return CACHE_TEXT.get(DictUtil.dictParentKey(type, value));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("使用 {} 存储数据字典信息", getClass().getName());
        }
    }
}
