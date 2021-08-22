package com.houkunlin.system.dict.starter.provider;

import com.houkunlin.system.dict.starter.DictRegistrar;
import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
import com.houkunlin.system.dict.starter.notice.RefreshDictEvent;
import com.houkunlin.system.dict.starter.store.DictStore;

import java.util.*;

/**
 * 系统字典提供者。系统扫描到的数据字典信息会先存储到 {@link SystemDictProvider} 来进行一个本地缓存，之后 {@link DictProvider} 中的字典信息会被被保存到 {@link DictStore} 对象中
 *
 * @author HouKunLin
 */
public interface DictProvider {
    /**
     * 在发起 {@link RefreshDictEvent} 刷新事件时，可以指定刷新某个或多个 {@link DictProvider} 对象，在 {@link DictRegistrar} 刷新字典时将调用此方法来判断是否刷新此 {@link DictProvider} 的字典数据
     *
     * @param refreshDictProviderClasses {@link RefreshDictEvent#dictProviderClasses} 刷新事件指定的 {@link DictProvider} 列表
     * @return 是否可刷新当前 {@link DictProvider} 提供的数据字典
     */
    default boolean supportRefresh(Set<String> refreshDictProviderClasses) {
        if (refreshDictProviderClasses == null || refreshDictProviderClasses.isEmpty()) {
            return true;
        }
        return refreshDictProviderClasses.contains(getClass().getName());
    }

    /**
     * 实现一个迭代器，可以通过迭代器直接获取到列表信息。
     * 建议在进行超大数据量的时候手动实现此方法，虽然有可能不存在这种使用场景。
     * 该方法不会并发执行。
     *
     * @return 迭代器对象
     */
    default Iterator<DictTypeVo> dictTypeIterator() {
        return Collections.emptyIterator();
    }

    /**
     * 实现一个迭代器，可以通过迭代器直接获取到列表信息。
     * 建议在进行超大数据量的时候手动实现此方法，虽然有可能不存在这种使用场景。
     * 该方法不会并发执行。
     *
     * @return 迭代器对象
     */
    default Iterator<DictValueVo> dictValueIterator() {
        final Iterator<DictTypeVo> iterator = dictTypeIterator();
        return new Iterator<DictValueVo>() {
            List<DictValueVo> valueVos = null;
            int index = 0;
            int size = 0;

            @Override
            public boolean hasNext() {
                if (index == size && iterator.hasNext()) {
                    valueVos = iterator.next().getChildren();
                    index = 0;
                    size = valueVos.size();
                }
                return valueVos != null && index < size;
            }

            @Override
            public DictValueVo next() {
                if (index >= size) {
                    throw new NoSuchElementException("没有更多的字典值对象");
                }
                return valueVos.get(index++);
            }
        };
    }
}
