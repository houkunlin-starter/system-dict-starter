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
     * 标记该字典提供者是否存储完整的字典类型信息。当字典数据特别多的时候，不建议把此项设置为 true ，因为数据量多会影响系统存储读取。
     * 系统字典 {@link SystemDictProvider} 默认存储字典类型对象信息。
     *
     * @return <p>true: 在缓存中存储完整的字典类型对象（含字典值列表对象），会调用 {@link #dictTypeIterator()} 方法获取数据</p>
     * <p>false: 会调用 {@link #dictValueIterator()} 方法获取数据，假如 {@link #dictValueIterator()} 未被实现类覆盖，则实际上就是调用 {@link #dictTypeIterator()} 方法</p>
     * @since 1.4.0
     */
    default boolean isStoreDictType() {
        return false;
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
