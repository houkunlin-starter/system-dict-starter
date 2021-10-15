package com.houkunlin.system.dict.starter.store;

import com.houkunlin.system.dict.starter.DictUtil;
import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.Iterator;
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
    private static final ConcurrentHashMap<String, DictTypeVo> CACHE_TYPE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> CACHE_TEXT = new ConcurrentHashMap<>();
    private final RemoteDict remoteDict;

    @Override
    public void store(final DictTypeVo dictType) {
        CACHE_TYPE.put(dictType.getType(), dictType);
    }

    @Override
    public void store(final Iterator<DictValueVo> iterator) {
        iterator.forEachRemaining(valueVo -> CACHE_TEXT.put(DictUtil.dictKey(valueVo), valueVo.getTitle()));
    }

    @Override
    public Set<String> dictTypeKeys() {
        return CACHE_TYPE.keySet();
    }

    @Override
    public DictTypeVo getDictType(final String type) {
        final DictTypeVo typeVo = CACHE_TYPE.get(type);
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
    public void afterPropertiesSet() throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("使用 {} 存储数据字典信息", getClass().getName());
        }
    }
}
