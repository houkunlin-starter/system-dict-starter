package com.houkunlin.system.dict.starter;

import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
import com.houkunlin.system.dict.starter.notice.RefreshDictEvent;
import com.houkunlin.system.dict.starter.provider.DictProvider;
import com.houkunlin.system.dict.starter.provider.SystemDictProvider;
import com.houkunlin.system.dict.starter.store.DictStore;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class DictRegistrar implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(DictRegistrar.class);
    private final List<DictProvider> providers;
    private final DictStore store;
    private final DictProperties properties;
    /**
     * 上一次刷新字典时间
     */
    private long lastModified = 0;

    public void refreshDict(Set<String> dictProviderClasses) {
        final long interval = System.currentTimeMillis() - lastModified;
        final long refreshDictInterval = properties.getRefreshDictInterval();
        if (interval < refreshDictInterval) {
            if (logger.isDebugEnabled()) {
                logger.debug("距离上一次刷新字典 {} ms，小于配置的 {} ms，本次事件将不会刷新字典", interval, refreshDictInterval);
            }
            return;
        }
        lastModified = System.currentTimeMillis();
        for (final DictProvider provider : providers) {
            if (!provider.supportRefresh(dictProviderClasses)) {
                continue;
            }
            if (provider instanceof SystemDictProvider) {
                // 系统字典特殊处理。系统字典的数据数量普遍情况下不大，因此直接存储影响不大
                final Iterator<? extends DictTypeVo> typeIterator = provider.dictTypeIterator();
                typeIterator.forEachRemaining(dictType -> {
                    // 系统字典直接写入完整的对象，因为在给前端做字典选择的时候需要完整的列表
                    storeDict(dictType);
                    // 同时系统字典的字典值列表也写入缓存
                    storeDict(dictType.getChildren().iterator());
                });
            } else {
                storeDict(provider.dictValueIterator());
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (properties.isOnBootRefreshDict()) {
            refreshDict(null);
        }
    }

    /**
     * 处理系统内部发起的刷新数据字典事件
     */
    @Async
    @EventListener
    public void eventListenerRefreshEvent(RefreshDictEvent event) {
        logger.info("[start] 应用内部通知刷新字典事件。事件内容：{}", event.getSource());
        refreshDict(event.getDictProviderClasses());
        logger.info("[finish] 应用内部通知刷新字典事件");
    }

    private void storeDict(Iterator<DictValueVo> iterator) {
        store.store(iterator);
    }

    private void storeDict(DictTypeVo dictType) {
        store.store(dictType);
    }
}
