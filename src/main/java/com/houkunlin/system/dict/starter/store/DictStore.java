package com.houkunlin.system.dict.starter.store;

import com.houkunlin.system.dict.starter.bean.DictType;
import com.houkunlin.system.dict.starter.bean.DictValue;
import com.houkunlin.system.dict.starter.provider.DictProvider;

import java.util.Iterator;
import java.util.Set;

/**
 * 系统字典存储对象。程序扫描到的系统字典信息都将会调用该对象来存储字典信息，同时该对象也将负责读取获取系统字典信息
 *
 * @author HouKunLin
 */
public interface DictStore {
    /**
     * 存储一个完整的数据字典信息
     *
     * @param dictType 数据字典对象
     */
    void store(DictType dictType);

    /**
     * 存储一个完整的数据字典信息（单独为存储系统字典定义一个方法）
     *
     * @param dictType 数据字典对象
     * @since 1.5.0
     */
    default void storeSystemDict(DictType dictType) {
        store(dictType);
    }

    /**
     * 存储一个字典值列表数据
     *
     * @param iterable 字典值列表
     * @since 1.5.0
     */
    default void store(Iterable<DictValue> iterable) {
        store(iterable.iterator());
    }

    /**
     * 存储一个字典值列表数据。
     * 为 Redis 保留的方法，数据量大时使用批量处理
     *
     * @param iterable 字典值列表
     * @since 1.5.3
     */
    default void storeBatch(Iterable<DictValue> iterable) {
        storeBatch(iterable.iterator());
    }

    /**
     * 存储一个字典值列表数据
     *
     * @param iterator 字典值列表
     */
    void store(Iterator<DictValue> iterator);

    /**
     * 存储一个字典值列表数据。
     * 为 Redis 保留的方法，数据量大时使用批量处理
     *
     * @param iterator 字典值列表
     * @since 1.5.3
     */
    default void storeBatch(Iterator<DictValue> iterator) {
        store(iterator);
    }

    /**
     * 删除一个字典类型（同时删除对应的字典值列表）
     *
     * @param dictType 字典类型代码
     */
    void removeDictType(final String dictType);

    /**
     * 字典类型代码列表（涵盖系统字典类型代码）。
     * <p>仅能够得到 {@link DictProvider#isStoreDictType()} 返回 true 的字典类型代码信息。</p>
     * <p>当 {@link DictProvider#isStoreDictType()} 返回 false 时对应的 {@link DictProvider} 提供的字典类型信息对象将不会被存储，也就是当前方法无法获得该字典类型代码</p>
     *
     * @return 字典类型代码列表（涵盖系统字典类型代码）
     * @since 1.4.0
     */
    Set<String> dictTypeKeys();

    /**
     * 系统字典类型代码列表。
     *
     * @return 字典类型代码列表（仅返回系统字典类型代码）
     * @since 1.5.0
     */
    default Set<String> systemDictTypeKeys() {
        return dictTypeKeys();
    }

    /**
     * 通过字典类型获取完整的字典信息
     * <p>仅能够得到 {@link DictProvider#isStoreDictType()} 返回 true 的字典类型代码信息。</p>
     * <p>当 {@link DictProvider#isStoreDictType()} 返回 false 时对应的 {@link DictProvider} 提供的字典类型信息对象将不会被存储，也就是当前方法无法获得该字典类型代码</p>
     *
     * @param type 字典类型
     * @return 字典对象信息
     */
    DictType getDictType(String type);

    /**
     * 通过字典值获取字典文本信息
     *
     * @param type  字典所属类型
     * @param value 字典值
     * @return 字典文本
     */
    String getDictText(String type, String value);

    /**
     * 获取字典父级值
     *
     * @param type  字典所属类型
     * @param value 字典值
     * @return 字典文本
     * @since 1.4.6
     */
    String getDictParentValue(String type, String value);
}
