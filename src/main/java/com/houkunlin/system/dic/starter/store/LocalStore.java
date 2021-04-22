package com.houkunlin.system.dic.starter.store;

import com.houkunlin.system.dic.starter.DicUtil;
import com.houkunlin.system.dic.starter.bean.DicTypeVo;
import com.houkunlin.system.dic.starter.bean.DicValueVo;
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
    private final RemoteDic remoteDic;

    public LocalStore(final RemoteDic remoteDic) {
        this.remoteDic = remoteDic;
    }

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
        final DicTypeVo typeVo = CACHE_TYPE.get(DicUtil.dicKey(type));
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
        logger.info("使用本地缓存存储数据字典信息");
    }
}