package com.houkunlin.system.dict.starter;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
import com.houkunlin.system.dict.starter.notice.RefreshDictEvent;
import com.houkunlin.system.dict.starter.notice.RefreshDictTypeEvent;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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
     * @since 1.4.4
     */
    @Async
    @EventListener
    public void refreshDictValueEvent(final RefreshDictValueEvent event) {
        final Iterable<DictValueVo> dictValueVos = event.getSource();
        store.store(dictValueVos.iterator());
        // 把字典值列表通过字典类型收集起来
        Multimap<String, DictValueVo> multimap = ArrayListMultimap.create();
        dictValueVos.forEach(valueVo -> multimap.put(valueVo.getDictType(), valueVo));
        final Set<String> keySet = multimap.keySet();
        for (final String dictType : keySet) {
            // 处理维护字典类型代码的字典信息
            maintainHandleDictType(dictType, new ArrayList<>(multimap.get(dictType)));
        }
    }

    /**
     * 维护处理字典类型信息
     *
     * @param dictType 字典类型代码
     * @param valueVos 字典值列表
     * @since 1.4.5
     */
    private void maintainHandleDictType(final String dictType, final List<DictValueVo> valueVos) {
        final DictTypeVo dictTypeVo = store.getDictType(dictType);
        final List<DictValueVo> valueVosResult = valueVos.stream().filter(vo -> vo.getTitle() != null).collect(Collectors.toList());
        if (dictTypeVo == null) {
            // 不存在字典类型信息，新增一个字典类型信息
            final DictTypeVo newType = new DictTypeVo("RefreshDictValueEvent Add", dictType, "RefreshDictValueEvent Add", valueVosResult);
            store.store(newType);
            return;
        }
        final List<DictValueVo> children = dictTypeVo.getChildren();
        if (children == null || children.isEmpty()) {
            // 原字典类型无字典值列表，增加新的字典值列表
            dictTypeVo.setChildren(valueVosResult);
            store.store(dictTypeVo);
            return;
        }
        final List<DictValueVo> valueVosRemove = valueVos.stream().filter(vo -> vo.getTitle() == null).collect(Collectors.toList());
        // 从列表移除需要删除的字典列表
        children.removeIf(vo1 -> {
            for (final DictValueVo vo2 : valueVosRemove) {
                if (Objects.equals(vo1.getValue(), vo2.getValue())) {
                    return true;
                }
            }
            return false;
        });
        // 维护需要替换的字典值列表信息
        children.addAll(valueVosResult);
        Map<Object, DictValueVo> map = new LinkedHashMap<>();
        children.forEach(valueVo -> map.put(valueVo.getValue(), valueVo));

        dictTypeVo.setChildren(new ArrayList<>(map.values()));
        store.store(dictTypeVo);
    }

    /**
     * 刷新单个字典值文本信息，不会刷新整个字典信息
     *
     * @param event 事件
     * @since 1.4.5
     */
    @Async
    @EventListener
    public void refreshDictValueEvent(final RefreshDictTypeEvent event) {
        final Iterable<DictTypeVo> dictTypeVos = event.getSource();
        dictTypeVos.forEach(dictType -> {
            final List<DictValueVo> dictValueVos = fixDictTypeChildren(dictType);
            store.store(dictType);
            store.store(dictValueVos.iterator());
        });
    }

    private List<DictValueVo> fixDictTypeChildren(final DictTypeVo dictTypeVo) {
        final List<DictValueVo> children = dictTypeVo.getChildren();
        if (children != null) {
            for (final DictValueVo valueVo : children) {
                valueVo.setDictType(dictTypeVo.getType());
            }
            return children;
        } else {
            dictTypeVo.setChildren(Collections.emptyList());
            return Collections.emptyList();
        }
    }
}
