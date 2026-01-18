package com.houkunlin.dict.provider;

import com.houkunlin.dict.DictRegistrar;
import com.houkunlin.dict.bean.DictType;
import com.houkunlin.dict.bean.DictValue;
import com.houkunlin.dict.notice.RefreshDictEvent;
import com.houkunlin.dict.store.DictStore;

import java.util.*;

/**
 * 数据字典提供者接口
 * <p>
 * 该接口定义了数据字典提供者的基本行为，所有字典数据源都需要实现此接口。
 * 系统扫描到的数据字典信息会先存储到 {@link SystemDictProvider} 进行本地缓存，
 * 之后所有 {@link DictProvider} 实现类中的字典信息会被保存到 {@link DictStore} 对象中。
 * 通过实现此接口，可以支持多种字典数据源，如数据库、枚举、配置文件等。
 * </p>
 *
 * @author HouKunLin
 * @since 1.0.0
 */
public interface DictProvider {
    /**
     * 判断是否支持刷新当前字典提供者的数据
     * <p>
     * 在发起 {@link RefreshDictEvent} 刷新事件时，可以指定刷新某个或多个 {@link DictProvider} 对象。
     * 在 {@link DictRegistrar} 刷新字典时将调用此方法来判断是否刷新此 {@link DictProvider} 的字典数据。
     * 默认实现检查当前类的全限定名是否在指定的刷新列表中。
     * </p>
     *
     * @param refreshDictProviderClasses {@link RefreshDictEvent#dictProviderClasses} 刷新事件指定的 {@link DictProvider} 类名列表
     * @return {@code true} 表示可以刷新当前字典提供者的数据，{@code false} 表示不刷新
     */
    default boolean supportRefresh(Set<String> refreshDictProviderClasses) {
        if (refreshDictProviderClasses == null || refreshDictProviderClasses.isEmpty()) {
            return true;
        }
        return refreshDictProviderClasses.contains(getClass().getName());
    }

    /**
     * 标记该字典提供者是否存储完整的字典类型信息
     * <p>
     * 当字典数据特别多的时候，不建议把此项设置为 {@code true}，因为数据量多会影响系统存储读取性能。
     * 系统字典 {@link SystemDictProvider} 默认存储完整的字典类型对象信息。
     * </p>
     * <p>
     * 返回 {@code true} 时：在缓存中存储完整的字典类型对象（含字典值列表对象），会调用 {@link #dictTypeIterator()} 方法获取数据。
     * 返回 {@code false} 时：会调用 {@link #dictValueIterator()} 方法获取数据，如果 {@link #dictValueIterator()} 未被实现类覆盖，
     * 则实际上会调用 {@link #dictTypeIterator()} 方法。
     * </p>
     *
     * @return {@code true} 表示存储完整的字典类型信息，{@code false} 表示不存储或只存储字典值信息
     * @since 1.4.0
     */
    default boolean isStoreDictType() {
        return false;
    }

    /**
     * 获取字典类型迭代器
     * <p>
     * 实现一个迭代器，可以通过迭代器直接获取到字典类型列表信息。
     * 建议在进行超大数据量的时候手动实现此方法，虽然有可能不存在这种使用场景。
     * 该方法不会并发执行，默认返回一个空迭代器。
     * </p>
     * <p>
     * 当 {@link #isStoreDictType()} 返回 {@code true} 时，字典注册器会调用此方法获取完整的字典类型信息。
     * </p>
     *
     * @return 字典类型对象的迭代器，默认返回空迭代器
     */
    default Iterator<DictType> dictTypeIterator() {
        return Collections.emptyIterator();
    }

    /**
     * 获取字典值迭代器
     * <p>
     * 实现一个迭代器，可以通过迭代器直接获取到字典值列表信息。
     * 建议在进行超大数据量的时候手动实现此方法，虽然有可能不存在这种使用场景。
     * 该方法不会并发执行，默认实现通过 {@link #dictTypeIterator()} 转换得到字典值迭代器。
     * </p>
     * <p>
     * 当 {@link #isStoreDictType()} 返回 {@code false} 时，字典注册器会调用此方法获取字典值信息。
     * 默认实现将字典类型迭代器转换为字典值迭代器，遍历所有字典类型中的字典值列表。
     * </p>
     *
     * @return 字典值对象的迭代器，默认通过字典类型迭代器转换得到
     */
    default Iterator<DictValue> dictValueIterator() {
        final Iterator<DictType> iterator = dictTypeIterator();
        return new Iterator<DictValue>() {
            List<DictValue> valueVos = null;
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
            public DictValue next() {
                if (index >= size) {
                    throw new NoSuchElementException("没有更多的字典值对象");
                }
                return valueVos.get(index++);
            }
        };
    }
}
