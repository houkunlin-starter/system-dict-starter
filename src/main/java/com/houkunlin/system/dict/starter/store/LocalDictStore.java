package com.houkunlin.system.dict.starter.store;

import com.houkunlin.system.dict.starter.DictUtil;
import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用本地 Map 来存储系统字典信息。当不存在 Redis 环境时默认使用该存储方式。
 *
 * @author HouKunLin
 */
public class LocalDictStore implements DictStore {
    private static final Logger logger = LoggerFactory.getLogger(LocalDictStore.class);
    private static final ConcurrentHashMap<String, DictTypeVo> CACHE_TYPE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> CACHE_TITLE = new ConcurrentHashMap<>();
    private final RemoteDict remoteDic;

    public LocalDictStore(final RemoteDict remoteDic) {
        this.remoteDic = remoteDic;
    }

    @Override
    public void store(final DictTypeVo dictType) {
        CACHE_TYPE.put(DictUtil.dictKey(dictType.getType()), dictType);
        if (logger.isDebugEnabled()) {
            logger.debug("当前 CACHE_TYPE Map 共有 {} 个字典类型信息", CACHE_TYPE.size());
        }
    }

    @Override
    public void store(final Iterator<DictValueVo> iterator) {
        iterator.forEachRemaining(valueVo -> CACHE_TITLE.put(DictUtil.dictKey(valueVo), valueVo.getTitle()));
        if (logger.isDebugEnabled()) {
            logger.debug("当前 CACHE_TITLE Map 共有 {} 个字典值信息", CACHE_TITLE.size());
        }
    }

    @Override
    public DictTypeVo getDictType(final String type) {
        final DictTypeVo typeVo = CACHE_TYPE.get(DictUtil.dictKey(type));
        if (typeVo != null) {
            return typeVo;
        }
        return remoteDic.getDictType(type);
    }

    @Override
    public String getDictText(final String type, final String value) {
        final String title = CACHE_TITLE.get(DictUtil.dictKey(type, value));
        if (title != null) {
            return title;
        }
        return remoteDic.getDictText(type, value);
    }

    @PostConstruct
    public void post() {
        logger.info("使用本地 Map 存储数据字典信息");
    }
}
