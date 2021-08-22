package com.houkunlin.system.dict.starter.store;

import com.houkunlin.system.dict.starter.DicUtil;
import com.houkunlin.system.dict.starter.bean.DicTypeVo;
import com.houkunlin.system.dict.starter.bean.DicValueVo;
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
public class LocalDicStore implements DicStore<Object> {
    private static final Logger logger = LoggerFactory.getLogger(LocalDicStore.class);
    private static final ConcurrentHashMap<String, DicTypeVo<Object>> CACHE_TYPE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> CACHE_TITLE = new ConcurrentHashMap<>();
    private final RemoteDic<Object> remoteDic;

    public LocalDicStore(final RemoteDic remoteDic) {
        this.remoteDic = remoteDic;
    }

    @Override
    public void store(final DicTypeVo<Object> dicType) {
        CACHE_TYPE.put(DicUtil.dicKey(dicType.getType()), dicType);
        if (logger.isDebugEnabled()) {
            logger.debug("当前 CACHE_TYPE Map 共有 {} 个字典类型信息", CACHE_TYPE.size());
        }
    }

    @Override
    public void store(final Iterator<DicValueVo<Object>> iterator) {
        iterator.forEachRemaining(valueVo -> CACHE_TITLE.put(DicUtil.dicKey(valueVo), valueVo.getTitle()));
        if (logger.isDebugEnabled()) {
            logger.debug("当前 CACHE_TITLE Map 共有 {} 个字典值信息", CACHE_TITLE.size());
        }
    }

    @Override
    public DicTypeVo<Object> getDicType(final String type) {
        final DicTypeVo<Object> typeVo = CACHE_TYPE.get(DicUtil.dicKey(type));
        if (typeVo != null) {
            return typeVo;
        }
        return remoteDic.getDicType(type);
    }

    @Override
    public String getDicValueTitle(final String type, final String value) {
        final String title = CACHE_TITLE.get(DicUtil.dicKey(type, value));
        if (title != null) {
            return title;
        }
        return remoteDic.getDicValueTitle(type, value);
    }

    @PostConstruct
    public void post() {
        logger.info("使用本地 Map 存储数据字典信息");
    }
}
