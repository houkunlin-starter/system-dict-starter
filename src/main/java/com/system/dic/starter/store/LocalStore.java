package com.system.dic.starter.store;

import com.system.dic.starter.DicUtil;
import com.system.dic.starter.bean.DicTypeVo;
import com.system.dic.starter.bean.DicValueVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author HouKunLin
 */
public class LocalStore implements DicStore {
    private static final Logger logger = LoggerFactory.getLogger(LocalStore.class);
    private static final ConcurrentHashMap<String, DicTypeVo> CACHE_TYPE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> CACHE_TITLE = new ConcurrentHashMap<>();

    @Override
    public void store(final DicTypeVo dicType) {
        CACHE_TYPE.put(DicUtil.dicKey(dicType.getType()), dicType);
    }

    @Override
    public void store(final Iterator<DicValueVo<? extends Serializable>> iterator) {
        iterator.forEachRemaining(valueVo -> CACHE_TITLE.put(DicUtil.dicKey(valueVo), valueVo.getTitle()));
    }

    @Override
    public DicTypeVo getDicType(final String type) {
        return CACHE_TYPE.get(DicUtil.dicKey(type));
    }

    @Override
    public Object getDicValueTitle(final String type, final String value) {
        return CACHE_TITLE.get(DicUtil.dicKey(type, value));
    }

    @PostConstruct
    public void post() {
        logger.info("使用本地缓存存储数据字典信息");
    }
}
