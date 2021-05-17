package com.houkunlin.system.dic.starter;

import com.houkunlin.system.dic.starter.bean.DicTypeVo;
import com.houkunlin.system.dic.starter.bean.DicValueVo;
import com.houkunlin.system.dic.starter.notice.RefreshDicEvent;
import com.houkunlin.system.dic.starter.provider.DicProvider;
import com.houkunlin.system.dic.starter.provider.SystemDicProvider;
import com.houkunlin.system.dic.starter.store.DicStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 字典注册，把字典发送到缓存中
 *
 * @author HouKunLin
 */
@Configuration
public class DicRegistrar implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(DicRegistrar.class);
    private final List<DicProvider<?>> providers;
    private final DicStore<Object> store;
    private final DicProperties dicProperties;
    /**
     * 上一次刷新字典时间
     */
    private long lastModified = 0;

    public DicRegistrar(final List<DicProvider<?>> providers, final DicStore store, final DicProperties dicProperties) {
        this.providers = providers;
        this.store = store;
        this.dicProperties = dicProperties;
    }

    public void refreshDic(Set<String> dicProviderClasses) {
        final long interval = System.currentTimeMillis() - lastModified;
        final long refreshDicInterval = dicProperties.getRefreshDicInterval();
        if (interval < refreshDicInterval) {
            if (logger.isDebugEnabled()) {
                logger.debug("距离上一次刷新字典 {} ms，小于配置的 {} ms，本次事件将不会刷新字典", interval, refreshDicInterval);
            }
            return;
        }
        lastModified = System.currentTimeMillis();
        for (final DicProvider<?> provider : providers) {
            if (!provider.supportRefresh(dicProviderClasses)) {
                continue;
            }
            if (provider instanceof SystemDicProvider) {
                // 系统字典特殊处理
                final Iterator<? extends DicTypeVo<?>> typeIterator = provider.dicTypeIterator();
                typeIterator.forEachRemaining(dicType -> {
                    // 系统字典直接写入完整的对象，因为在给前端做字典选择的时候需要完整的列表
                    storeDic(dicType);
                    // 同时系统字典的字典值列表也写入缓存
                    storeDic(dicType.getChildren().iterator());
                });
            } else {
                storeDic(provider.dicValueIterator());
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (dicProperties.isOnBootRefreshDic()) {
            refreshDic(null);
        }
    }

    /**
     * 处理系统内部发起的刷新数据字典事件
     */
    @EventListener
    public void eventListenerRefreshDicEvent(RefreshDicEvent event) throws Exception {
        logger.info("[start] 应用内部通知刷新字典事件。事件内容：{}", event.getSource());
        refreshDic(event.getDicProviderClasses());
        logger.info("[finish] 应用内部通知刷新字典事件");
    }

    private void storeDic(Iterator<? extends DicValueVo<?>> iterator) {
        store.store((Iterator<DicValueVo<Object>>) iterator);
    }

    private void storeDic(DicTypeVo<?> dicType) {
        store.store((DicTypeVo<Object>) dicType);
    }
}
