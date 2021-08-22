package com.houkunlin.system.dict.starter;

import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
import com.houkunlin.system.dict.starter.notice.RefreshDictEvent;
import com.houkunlin.system.dict.starter.provider.DictProvider;
import com.houkunlin.system.dict.starter.provider.SystemDictProvider;
import com.houkunlin.system.dict.starter.store.DictStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 字典注册，把字典发送到缓存中
 *
 * @author HouKunLin
 */
@Configuration
public class DictRegistrar implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(DictRegistrar.class);
    private final List<DictProvider<?>> providers;
    private final DictStore<Object> store;
    private final DictProperties properties;
    /**
     * 上一次刷新字典时间
     */
    private long lastModified = 0;

    public DictRegistrar(final List<DictProvider<?>> providers, final DictStore store, final DictProperties properties) {
        this.providers = providers;
        this.store = store;
        this.properties = properties;
    }

    public void refreshDic(Set<String> dictProviderClasses) {
        final long interval = System.currentTimeMillis() - lastModified;
        final long refreshDictInterval = properties.getRefreshDictInterval();
        if (interval < refreshDictInterval) {
            if (logger.isDebugEnabled()) {
                logger.debug("距离上一次刷新字典 {} ms，小于配置的 {} ms，本次事件将不会刷新字典", interval, refreshDictInterval);
            }
            return;
        }
        lastModified = System.currentTimeMillis();
        for (final DictProvider<?> provider : providers) {
            if (!provider.supportRefresh(dictProviderClasses)) {
                continue;
            }
            if (provider instanceof SystemDictProvider) {
                // 系统字典特殊处理
                final Iterator<? extends DictTypeVo<?>> typeIterator = provider.dicTypeIterator();
                typeIterator.forEachRemaining(dictType -> {
                    // 系统字典直接写入完整的对象，因为在给前端做字典选择的时候需要完整的列表
                    storeDic(dictType);
                    // 同时系统字典的字典值列表也写入缓存
                    storeDic(dictType.getChildren().iterator());
                });
            } else {
                storeDic(provider.dicValueIterator());
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (properties.isOnBootRefreshDict()) {
            refreshDic(null);
        }
    }

    /**
     * 处理系统内部发起的刷新数据字典事件
     */
    @Async
    @EventListener
    public void eventListenerRefreshDicEvent(RefreshDictEvent event) throws Exception {
        logger.info("[start] 应用内部通知刷新字典事件。事件内容：{}", event.getSource());
        refreshDic(event.getDictProviderClasses());
        logger.info("[finish] 应用内部通知刷新字典事件");
    }

    private void storeDic(Iterator<? extends DictValueVo<?>> iterator) {
        store.store((Iterator<DictValueVo<Object>>) iterator);
    }

    private void storeDic(DictTypeVo<?> dictType) {
        store.store((DictTypeVo<Object>) dictType);
    }
}
