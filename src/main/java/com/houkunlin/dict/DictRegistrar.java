package com.houkunlin.dict;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.houkunlin.dict.bean.DictType;
import com.houkunlin.dict.bean.DictValue;
import com.houkunlin.dict.notice.RefreshDictEvent;
import com.houkunlin.dict.notice.RefreshDictTypeEvent;
import com.houkunlin.dict.notice.RefreshDictValueEvent;
import com.houkunlin.dict.properties.DictProperties;
import com.houkunlin.dict.provider.DictProvider;
import com.houkunlin.dict.provider.SystemDictProvider;
import com.houkunlin.dict.store.DictStore;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 字典注册器
 * <p>
 * 该类负责管理字典提供者和刷新字典数据，将字典数据发送到缓存中。
 * 主要功能包括：
 * <ul>
 * <li>初始化时加载字典数据</li>
 * <li>处理字典刷新事件</li>
 * <li>批量处理字典数据</li>
 * <li>维护字典类型和字典值的关系</li>
 * <li>支持异步刷新字典</li>
 * </ul>
 * 该类实现了 InitializingBean 接口，在 Spring 容器初始化完成后自动加载字典数据。
 * </p>
 *
 * @author HouKunLin
 * @since 1.0.0
 */
@Data
@Configuration
@RequiredArgsConstructor
public class DictRegistrar implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(DictRegistrar.class);
    /**
     * 数据字典信息提供商
     * <p>负责提供字典数据的来源，支持多个不同的字典提供者。</p>
     */
    private final List<DictProvider> providers;
    /**
     * 数据字典信息存储器
     * <p>负责字典数据的实际存储和读取操作，支持不同的存储实现。</p>
     */
    private final DictStore store;
    /**
     * 数据字典配置信息对象
     * <p>包含字典系统的各种配置参数，如刷新间隔、批处理大小等。</p>
     */
    private final DictProperties properties;
    /**
     * 上一次刷新字典时间
     * <p>用于控制字典刷新频率，避免过于频繁的刷新操作。</p>
     */
    private final AtomicLong lastModified = new AtomicLong(0);
    /**
     * 刷新单个字典事件：数据字典值文本数量超过 5 个就使用批量保存
     * <p>用于优化单个字典值刷新时的性能，当数据量较大时采用批量保存方式。</p>
     */
    private int valueEventBatchSize = 5;
    /**
     * 刷新整个字典事件：数据字典值文本分批次保存，每批次保存 1000 个
     * <p>用于优化整个字典刷新时的性能，避免一次性处理过多数据导致内存问题。</p>
     */
    private int typeEventBatchSize = 1000;

    /**
     * 刷新数据字典信息
     * <p>
     * 该方法用于刷新数据字典信息，首先检查距离上一次刷新的时间间隔，
     * 如果小于配置的刷新间隔，则跳过本次刷新操作。
     * 然后更新最后刷新时间，并调用 forEachAllDict 方法获取所有字典数据，
     * 最后将获取到的字典数据存储到 DictStore 中。
     * 如果开启了调试日志，则会记录刷新操作的耗时。
     * </p>
     *
     * @param dictProviderClasses 需要刷新的数据字典提供商类限定名，null 表示刷新所有
     */
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
        if (!logger.isDebugEnabled()) {
            forEachAllDict(dictProviderClasses, store::store, store::storeSystemDict, store::storeBatch);
        } else {
            long startTime = System.nanoTime();
            forEachAllDict(dictProviderClasses, store::store, store::storeSystemDict, store::storeBatch);
            logger.debug("本次刷新数据字典耗时 {} ms，传入刷新范围：{}", (System.nanoTime() - startTime) / 100_0000.0, dictProviderClasses);
        }
    }

    /**
     * 循环获取所有 {@link DictProvider} 字典提供者提供的所有字典数据信息，把获取到的字典对象和字典值数据存入到 {@link DictStore} 存储对象中
     *
     * @param dictProviderClasses 只获取特定的 {@link DictProvider} 数据，会调用 {@link DictProvider#supportRefresh(Set)} 来判断
     * @param dictTypeConsumer    保存字典类型的方法
     * @param dictValueConsumer   保存字典值数据的方法
     * @since 1.4.11
     */
    public void forEachAllDict(final Set<String> dictProviderClasses, final Consumer<DictType> dictTypeConsumer, final Consumer<DictType> systemDictTypeConsumer, final Consumer<Iterator<DictValue>> dictValueConsumer) {
        for (final DictProvider provider : providers) {
            if (!provider.supportRefresh(dictProviderClasses)) {
                continue;
            }
            // 根据 Provider 参数决定是否存储完整的字典类型信息对象
            if (provider.isStoreDictType()) {
                final Iterator<? extends DictType> typeIterator = provider.dictTypeIterator();
                final boolean isSystemProvider = provider instanceof SystemDictProvider;
                final List<DictValue> batchSaveDictValues = new ArrayList<>(typeEventBatchSize + 50);
                typeIterator.forEachRemaining(dictType -> {
                    dictTypeConsumer.accept(dictType);
                    if (isSystemProvider) {
                        // 系统字典单独存储一份
                        systemDictTypeConsumer.accept(dictType);
                    }
                    final List<DictValue> valueVos = fixDictTypeChildren(dictType.getType(), dictType.getChildren());
                    if (valueVos != null) {
                        batchSaveDictValues.addAll(valueVos);
                        if (batchSaveDictValues.size() > typeEventBatchSize) {
                            dictValueConsumer.accept(batchSaveDictValues.iterator());
                            batchSaveDictValues.clear();
                        }
                    }
                });
                if (!batchSaveDictValues.isEmpty()) {
                    dictValueConsumer.accept(batchSaveDictValues.iterator());
                }
            } else {
                dictValueConsumer.accept(provider.dictValueIterator());
            }
        }
    }

    /**
     * 在 Spring 容器初始化完成后执行
     * <p>
     * 该方法实现了 InitializingBean 接口的 afterPropertiesSet 方法，
     * 在 Spring 容器初始化完成后被调用。
     * 方法会检查配置文件中是否开启了启动时刷新字典的功能，
     * 如果开启，则调用 refreshDict 方法刷新所有字典数据。
     * </p>
     *
     * @throws Exception 如果执行过程中发生异常
     * @see InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (properties.isOnBootRefreshDict()) {
            refreshDict(null);
        }
    }

    /**
     * 处理系统内部发起的刷新数据字典事件
     * <p>
     * 该方法是一个异步事件监听器，用于处理系统内部发起的刷新数据字典事件。
     * 当收到 RefreshDictEvent 事件时，会调用 refreshDict 方法刷新字典数据。
     * 如果开启了调试日志，则会记录事件的内容。
     * </p>
     *
     * @param event RefreshDictEvent 事件
     */
    @Async
    @EventListener
    public void eventListenerRefreshEvent(final RefreshDictEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("[RefreshDictEvent] 应用内部通知刷新字典事件。事件内容：{}", event.getSource());
        }
        refreshDict(event.getDictProviderClasses());
    }

    /**
     * 刷新单个字典值文本信息
     * <p>
     * 该方法是一个异步事件监听器，用于处理单个字典值文本信息的刷新事件。
     * 当收到 RefreshDictValueEvent 事件时，会获取事件中的字典值列表，
     * 然后从中移除属于系统字典的文本信息（系统字典不允许通过事件方式刷新），
     * 最后根据字典值数量决定采用批量保存还是单个保存的方式将字典值存储到 DictStore 中。
     * </p>
     *
     * @param event RefreshDictValueEvent 事件
     * @since 1.4.4
     */
    @Async
    @EventListener
    public void refreshDictValueEvent(final RefreshDictValueEvent event) {
        final Iterable<DictValue> dictValueVos = event.getSource();
        ArrayList<DictValue> list;
        if (dictValueVos instanceof Collection) {
            list = new ArrayList<>((Collection<DictValue>) dictValueVos);
        } else {
            list = new ArrayList<>();
            for (DictValue valueVo : dictValueVos) {
                list.add(valueVo);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("[RefreshDictValueEvent.value] 刷新字典值文本信息，共有 {} 条数据", list.size());
        }
        Set<String> systemDictTypeKeys = store.systemDictTypeKeys();
        removeSystemDictValue(list.iterator(), systemDictTypeKeys);
        if (!list.isEmpty()) {
            if (list.size() > valueEventBatchSize) {
                // 刷新数据字典值文本时，超过5条数据的采用批量写入方式
                store.storeBatch(list.iterator());
            } else {
                store.store(list.iterator());
            }
        }
    }

    /**
     * 检测需要刷新的字典文本列表，从中移除属于系统字典的文本信息
     * <p>
     * 该方法用于从字典值列表中移除属于系统字典的文本信息，
     * 因为系统字典不允许通过事件方式刷新字典值。
     * 如果检测到系统字典类型，则会记录调试日志并移除该字典值。
     * </p>
     *
     * @param iterator           字典值迭代器
     * @param systemDictTypeKeys 系统字典代码列表
     * @since 1.5.0
     */
    private void removeSystemDictValue(Iterator<DictValue> iterator, Set<String> systemDictTypeKeys) {
        while (iterator.hasNext()) {
            DictValue valueVo = iterator.next();
            String dictType = valueVo.getDictType();
            if (systemDictTypeKeys.contains(dictType)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("[RefreshDictValueEvent.value] 刷新字典值涉及的字典类型代码 {}，此类型为系统字典类型，无法通过事件方式刷新字典值，已忽略", dictType);
                }
                iterator.remove();
            }
        }
    }

    /**
     * 刷新单个字典值文本信息并维护字典类型对象
     * <p>
     * 该方法是一个异步事件监听器，用于处理单个字典值文本信息的刷新事件，
     * 并根据 RefreshDictValueEvent.isUpdateDictType() 参数决定是否维护字典类型对象里面的字典值列表信息。
     * 当需要更新字典类型时，会将字典值按字典类型分组，然后对每个字典类型调用 maintainHandleDictType 方法进行处理。
     * </p>
     *
     * @param event RefreshDictValueEvent 事件
     * @since 1.4.5
     */
    @Async
    @EventListener
    public void refreshDictValueEventUpdateDictType(final RefreshDictValueEvent event) {
        if (!event.isUpdateDictType()) {
            return;
        }
        final Iterable<DictValue> dictValueVos = event.getSource();
        final boolean removeDictType = event.isRemoveDictType();
        // 把字典值列表通过字典类型收集起来
        Multimap<String, DictValue> multimap = ArrayListMultimap.create();
        dictValueVos.forEach(valueVo -> multimap.put(valueVo.getDictType(), valueVo));
        final Set<String> keySet = multimap.keySet();
        if (logger.isDebugEnabled()) {
            logger.debug("[RefreshDictValueEvent.type] 刷新字典值涉及的字典类型代码 {}", keySet);
        }
        Set<String> systemDictTypeKeys = store.systemDictTypeKeys();
        for (final String dictType : keySet) {
            if (systemDictTypeKeys.contains(dictType)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("[RefreshDictValueEvent.type] 刷新字典值涉及的字典类型代码 {}，此类型为系统字典类型，无法通过事件方式刷新字典值，已忽略", dictType);
                }
                continue;
            }
            // 处理维护字典类型代码的字典信息
            maintainHandleDictType(dictType, new ArrayList<>(multimap.get(dictType)), removeDictType);
        }
    }

    /**
     * 维护处理字典类型信息
     * <p>
     * 该方法用于维护处理字典类型信息，根据字典值列表更新字典类型对象。
     * 处理逻辑如下：
     * 1. 如果字典类型不存在，则创建一个新的字典类型
     * 2. 如果字典类型存在但没有字典值列表，则直接设置字典值列表
     * 3. 如果字典类型存在且有字典值列表，则调用 maintainHandleDictTypeDiffUpdate 方法处理差异
     * </p>
     *
     * @param dictType       字典类型代码
     * @param valueVos       字典值列表
     * @param removeDictType 没有字典值列表时是否删除字典类型
     * @since 1.4.5
     */
    private void maintainHandleDictType(final String dictType, final List<DictValue> valueVos, final boolean removeDictType) {
        final DictType dictTypeVo = store.getDictType(dictType);
        final List<DictValue> valueVosUpdate = valueVos.stream().filter(vo -> vo.getTitle() != null).collect(Collectors.toList());
        if (dictTypeVo == null) {
            // 不存在字典类型信息，新增一个字典类型信息
            final DictType newType = new DictType("RefreshDictValueEvent Add", dictType, "RefreshDictValueEvent Add", valueVosUpdate);
            store.store(newType);
            if (logger.isDebugEnabled()) {
                logger.debug("[RefreshDictValueEvent.type] 有一个新的字典类型被加入到缓存中 {}", newType);
            }
            return;
        }
        final List<DictValue> children = dictTypeVo.getChildren();
        if (children == null || children.isEmpty()) {
            // 原字典类型无字典值列表，增加新的字典值列表
            dictTypeVo.setChildren(valueVosUpdate);
            store.store(dictTypeVo);
            if (logger.isDebugEnabled()) {
                logger.debug("[RefreshDictValueEvent.type] 旧字典类型无字典值，更新后字典类型有字典值 {}", dictTypeVo);
            }
            return;
        }
        final List<DictValue> valueVosRemove = valueVos.stream().filter(vo -> vo.getTitle() == null).collect(Collectors.toList());
        maintainHandleDictTypeDiffUpdate(dictTypeVo, valueVosUpdate, valueVosRemove, removeDictType);
    }

    /**
     * 维护处理字典类型信息（处理字典值列表差异合并）
     * <p>
     * 该方法用于处理字典类型信息中的字典值列表差异合并，具体步骤如下：
     * 1. 从现有字典值列表中移除需要删除的字典值
     * 2. 向现有字典值列表中添加需要更新或新增的字典值
     * 3. 使用 LinkedHashMap 去重，保持字典值的顺序
     * 4. 根据 removeDictType 参数决定当字典值列表为空时是否删除字典类型
     * 5. 最后将更新后的字典类型存储到 DictStore 中
     * </p>
     *
     * @param dictType         字典类型对象
     * @param valueVosUpdate   需要更新或新增的字典值列表
     * @param valueVosRemove   需要删除的字典值列表
     * @param removeDictType   没有字典值列表时是否删除字典类型
     * @since 1.4.5.1
     */
    private void maintainHandleDictTypeDiffUpdate(final DictType dictType, final List<DictValue> valueVosUpdate, final List<DictValue> valueVosRemove, final boolean removeDictType) {
        final List<DictValue> children = dictType.getChildren();
        // 从列表移除需要删除的字典列表
        children.removeIf(vo1 -> {
            for (final DictValue vo2 : valueVosRemove) {
                if (Objects.equals(vo1.getValue(), vo2.getValue())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("[RefreshDictValueEvent.type] 字典类型 {} 的 {} = {} 字典值被删除", dictType.getType(), vo1.getValue(), vo1.getTitle());
                    }
                    return true;
                }
            }
            return false;
        });
        // 维护需要替换的字典值列表信息
        children.addAll(valueVosUpdate);
        Map<Object, DictValue> map = new LinkedHashMap<>();
        children.forEach(valueVo -> map.put(valueVo.getValue(), valueVo));

        dictType.setChildren(removeDictType && map.isEmpty() ? null : new ArrayList<>(map.values()));
        if (logger.isDebugEnabled()) {
            logger.debug("[RefreshDictValueEvent.type] 字典类型的字典值列表被更新，共有 {} 条数据", dictType.getChildren().size());
        }
        store.store(dictType);
    }

    /**
     * 刷新单个字典值类型信息（包含此字典类型的字典值列表）
     * <p>
     * 该方法是一个异步事件监听器，用于处理单个字典类型信息的刷新事件。
     * 当收到 RefreshDictTypeEvent 事件时，会获取事件中的字典类型列表，
     * 然后从中移除属于系统字典的类型信息（系统字典不允许通过事件方式刷新），
     * 最后修复字典类型的字典值列表信息并存储到 DictStore 中。
     * </p>
     *
     * @param event RefreshDictTypeEvent 事件
     * @since 1.4.5
     */
    @Async
    @EventListener
    public void refreshDictTypeEvent(final RefreshDictTypeEvent event) {
        final Iterable<DictType> dictTypeVos = event.getSource();
        Set<String> systemDictTypeKeys = store.systemDictTypeKeys();
        dictTypeVos.forEach(dictType -> {
            if (systemDictTypeKeys.contains(dictType.getType())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("[RefreshDictTypeEvent] 刷新字典值涉及的字典类型代码 {}，此类型为系统字典类型，无法通过事件方式刷新字典值，已忽略", dictType.getType());
                }
                return;
            }
            final List<DictValue> dictValues = fixDictTypeChildren(dictType.getType(), dictType.getChildren());
            if (dictValues != null) {
                store.removeDictType(dictType.getType());
                store.store(dictValues.iterator());
            }
            store.store(dictType);
        });
    }

    /**
     * 修复数据字典类型的字典项列表信息
     * <p>
     * 该方法用于修复数据字典类型的字典项列表信息，确保每个字典值都设置了正确的字典类型代码。
     * 如果字典值列表为 null，则直接返回 null。
     * </p>
     *
     * @param dictType     数据字典类型代码
     * @param dictValues 字典值列表
     * @return 修复后的字典值列表
     */
    @Nullable
    private List<DictValue> fixDictTypeChildren(final String dictType, final List<DictValue> dictValues) {
        if (dictValues == null) {
            return null;
        }
        for (final DictValue valueVo : dictValues) {
            valueVo.setDictType(dictType);
        }
        return dictValues;
    }
}
