package com.houkunlin.system.dict.starter.provider;

import com.houkunlin.system.dict.starter.DicRegistrar;
import com.houkunlin.system.dict.starter.bean.DicTypeVo;
import com.houkunlin.system.dict.starter.bean.DicValueVo;
import com.houkunlin.system.dict.starter.notice.RefreshDicEvent;
import com.houkunlin.system.dict.starter.store.DicStore;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 系统字典提供者。系统扫描到的数据字典信息会先存储到 {@link SystemDicProvider} 来进行一个本地缓存，之后 {@link DicProvider} 中的字典信息会被被保存到 {@link DicStore} 对象中
 *
 * @author HouKunLin
 */
public interface DicProvider<V> {
    /**
     * 在发起 {@link RefreshDicEvent} 刷新事件时，可以指定刷新某个或多个 DicProvider 对象，在 {@link DicRegistrar} 刷新字典时将调用此方法来判断是否刷新此 DicProvider 的字典数据
     *
     * @param refreshDicProviderClasses {@link RefreshDicEvent#dicProviderClasses} 刷新事件指定的 DicProvider 列表
     * @return 是否可刷新当前 DicProvider 提供的数据字典
     */
    default boolean supportRefresh(Set<String> refreshDicProviderClasses) {
        if (refreshDicProviderClasses == null || refreshDicProviderClasses.isEmpty()) {
            return true;
        }
        return refreshDicProviderClasses.contains(getClass().getName());
    }

    /**
     * 实现一个迭代器，可以通过迭代器直接获取到列表信息。
     * 建议在进行超大数据量的时候手动实现此方法，虽然有可能不存在这种使用场景。
     * 该方法不会并发执行。
     *
     * @return 迭代器对象
     */
    default Iterator<DicTypeVo<V>> dicTypeIterator() {
        return Collections.emptyIterator();
    }

    /**
     * 实现一个迭代器，可以通过迭代器直接获取到列表信息。
     * 建议在进行超大数据量的时候手动实现此方法，虽然有可能不存在这种使用场景。
     * 该方法不会并发执行。
     *
     * @return 迭代器对象
     */
    default Iterator<DicValueVo<V>> dicValueIterator() {
        final Iterator<DicTypeVo<V>> iterator = dicTypeIterator();
        return new Iterator<DicValueVo<V>>() {
            List<DicValueVo<V>> valueVos = null;
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
            public DicValueVo<V> next() {
                return valueVos.get(index++);
            }
        };
    }
}
