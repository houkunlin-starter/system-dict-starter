package com.houkunlin.system.dict.starter;

import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
import com.houkunlin.system.dict.starter.notice.RefreshDictEvent;
import com.houkunlin.system.dict.starter.notice.RefreshDictValueEvent;
import com.houkunlin.system.dict.starter.properties.DictProperties;
import com.houkunlin.system.dict.starter.provider.DictProvider;
import com.houkunlin.system.dict.starter.store.DictStore;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

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
    private final AtomicLong lastModified = new AtomicLong(0);

    public void refreshDict(Set<String> dictProviderClasses) {
        final long interval = System.currentTimeMillis() - lastModified.get();
        final Duration refreshDictInterval = properties.getRefreshDictInterval();
        if (interval < refreshDictInterval.toMillis()) {
            if (logger.isDebugEnabled()) {
                logger.debug("距离上一次刷新字典 {} ms，小于配置的 {} ms，本次事件将不会刷新字典", interval, refreshDictInterval);
            }
            return;
        }
        lastModified.set(System.currentTimeMillis());
        for (final DictProvider provider : providers) {
            if (!provider.supportRefresh(dictProviderClasses)) {
                continue;
            }
            // 根据 Provider 参数决定是否存储完整的字典类型信息对象
            if (provider.isStoreDictType()) {
                final Iterator<? extends DictTypeVo> typeIterator = provider.dictTypeIterator();
                typeIterator.forEachRemaining(dictType -> {
                    store.store(dictType);
                    store.store(dictType.getChildren().iterator());
                });
            } else {
                store.store(provider.dictValueIterator());
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
    public void eventListenerRefreshEvent(final RefreshDictEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("[start] 应用内部通知刷新字典事件。事件内容：{}", event.getSource());
        }
        refreshDict(event.getDictProviderClasses());
        logger.debug("[finish] 应用内部通知刷新字典事件");
    }

    /**
     * 刷新单个字典值文本信息，不会刷新整个字典信息
     *
     * @param event 事件
     * @since 1.4.3.4
     */
    @Async
    @EventListener
    public void refreshDictValueEvent(final RefreshDictValueEvent event) {
        final Iterable<DictValueVo> dictValueVos = event.getSource();
        store.store(dictValueVos.iterator());
    }
}
