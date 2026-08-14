package com.houkunlin.dict;

import com.houkunlin.dict.bean.DictType;
import com.houkunlin.dict.bean.DictValue;
import com.houkunlin.dict.provider.DictProvider;
import com.houkunlin.dict.store.DictStore;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 数据字典注册器接口
 * <p>
 * 定义数据字典注册与刷新行为，负责从各个 {@link DictProvider} 数据源收集字典数据，
 * 并提供完整的字典刷新能力。具体实现由 {@code DictRegistrarImpl} 提供。
 * </p>
 *
 * @author HouKunLin
 */
public interface DictRegistrar {
    /**
     * 循环获取所有 {@link DictProvider} 字典提供者提供的所有字典数据信息
     * <p>
     * 遍历所有支持刷新的字典提供者，将获取到的字典类型和字典值数据通过回调消费。
     * 当 {@link DictProvider#isStoreDictType()} 返回 {@code true} 时存储完整的字典类型信息，
     * 否则只存储字典值信息。
     * </p>
     *
     * @param dictProviderClasses      只获取特定的 {@link DictProvider} 数据，null 表示刷新所有
     * @param dictTypeConsumer         保存字典类型的方法
     * @param systemDictTypeConsumer   保存系统字典类型的方法
     * @param dictValueConsumer        保存字典值数据的方法
     */
    void forEachAllDict(Set<String> dictProviderClasses, Consumer<DictType> dictTypeConsumer, Consumer<DictType> systemDictTypeConsumer, Consumer<Iterator<DictValue>> dictValueConsumer);

    /**
     * 刷新数据字典信息
     * <p>
     * 该方法用于刷新数据字典信息，首先检查距离上一次刷新的时间间隔，
     * 如果小于配置的刷新间隔，则跳过本次刷新操作。
     * 然后更新最后刷新时间，并调用 {@link #forEachAllDict} 方法获取所有字典数据，
     * 最后将获取到的字典数据存储到 {@link DictStore} 中。
     * </p>
     *
     * @param dictProviderClasses 需要刷新的数据字典提供商类限定名，null 表示刷新所有
     */
    void refreshDict(Set<String> dictProviderClasses);
}
