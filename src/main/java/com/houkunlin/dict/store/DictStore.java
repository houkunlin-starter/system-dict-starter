package com.houkunlin.dict.store;

import com.houkunlin.dict.bean.DictType;
import com.houkunlin.dict.bean.DictValue;
import com.houkunlin.dict.provider.DictProvider;

import java.util.Iterator;
import java.util.Set;

/**
 * 系统字典存储接口
 * <p>
 * 该接口定义了数据字典存储的基本操作，包括存储、删除和查询字典数据。
 * 程序扫描到的系统字典信息都会调用该接口的实现类来存储字典信息，
 * 同时该接口的实现类也负责读取和获取系统字典信息。
 * 系统支持多种存储实现，如本地内存存储和Redis分布式存储。
 * </p>
 *
 * @author HouKunLin
 * @since 1.0.0
 */
public interface DictStore {
    /**
     * 存储一个完整的数据字典信息
     * <p>
     * 将包含字典类型和所有字典值的完整字典对象存储到存储系统中。
     * 如果字典值列表为 {@code null}，则会删除该字典类型及其所有字典值。
     * </p>
     *
     * @param dictType 数据字典对象，包含字典类型代码和字典值列表
     */
    void store(DictType dictType);

    /**
     * 存储一个完整的系统字典信息
     * <p>
     * 专门为存储系统字典定义的方法，系统字典通常指由系统自动生成的字典类型，
     * 如枚举转换的字典等。默认实现调用 {@link #store(DictType)} 方法。
     * </p>
     *
     * @param dictType 系统字典对象，包含字典类型代码和字典值列表
     * @since 1.5.0
     */
    default void storeSystemDict(DictType dictType) {
        store(dictType);
    }

    /**
     * 存储一个字典值列表数据
     * <p>
     * 批量存储字典值对象，适用于需要单独存储字典值而不需要完整字典类型信息的场景。
     * 默认实现将 {@link Iterable} 转换为 {@link Iterator} 后调用 {@link #store(Iterator)} 方法。
     * </p>
     *
     * @param iterable 字典值列表，包含多个字典值对象
     * @since 1.5.0
     */
    default void store(Iterable<DictValue> iterable) {
        store(iterable.iterator());
    }

    /**
     * 批量存储字典值列表数据
     * <p>
     * 专门为Redis存储优化设计的方法，当数据量较大时使用批量处理提高性能。
     * 默认实现将 {@link Iterable} 转换为 {@link Iterator} 后调用 {@link #storeBatch(Iterator)} 方法。
     * </p>
     *
     * @param iterable 字典值列表，包含多个字典值对象
     * @since 1.5.3
     */
    default void storeBatch(Iterable<DictValue> iterable) {
        storeBatch(iterable.iterator());
    }

    /**
     * 存储字典值迭代器数据
     * <p>
     * 通过迭代器批量存储字典值对象，适用于流式处理大量字典数据的场景。
     * 实现类需要处理字典值对象的存储逻辑，包括字典文本和父级值的存储。
     * </p>
     *
     * @param iterator 字典值迭代器，用于遍历多个字典值对象
     */
    void store(Iterator<DictValue> iterator);

    /**
     * 批量存储字典值迭代器数据
     * <p>
     * 专门为Redis存储优化设计的方法，当数据量较大时使用批量处理提高性能。
     * 默认实现调用 {@link #store(Iterator)} 方法，但实现类可以覆盖此方法以提供更高效的批量处理。
     * </p>
     *
     * @param iterator 字典值迭代器，用于遍历多个字典值对象
     * @since 1.5.3
     */
    default void storeBatch(Iterator<DictValue> iterator) {
        store(iterator);
    }

    /**
     * 删除一个字典类型及其所有字典值
     * <p>
     * 从存储系统中删除指定的字典类型，同时删除该字典类型对应的所有字典值数据。
     * 该方法用于清理不再需要的字典数据。
     * </p>
     *
     * @param dictType 字典类型代码，标识要删除的字典类型
     */
    void removeDictType(final String dictType);

    /**
     * 获取所有字典类型代码列表
     * <p>
     * 返回存储系统中所有字典类型的代码集合，包括系统字典类型代码。
     * 注意：仅能获取到 {@link DictProvider#isStoreDictType()} 返回 {@code true} 的字典类型代码信息。
     * 当 {@link DictProvider#isStoreDictType()} 返回 {@code false} 时，对应的 {@link DictProvider}
     * 提供的字典类型信息对象将不会被存储，因此当前方法无法获得该字典类型代码。
     * </p>
     *
     * @return 字典类型代码集合，包含所有已存储的字典类型代码
     * @since 1.4.0
     */
    Set<String> dictTypeKeys();

    /**
     * 获取系统字典类型代码列表
     * <p>
     * 专门获取系统字典类型代码的方法，系统字典通常指由系统自动生成的字典类型，
     * 如枚举转换的字典等。默认实现调用 {@link #dictTypeKeys()} 方法，
     * 但具体实现类可以覆盖此方法以提供专门的系统字典类型查询。
     * </p>
     *
     * @return 系统字典类型代码集合，仅包含系统字典类型代码
     * @since 1.5.0
     */
    default Set<String> systemDictTypeKeys() {
        return dictTypeKeys();
    }

    /**
     * 通过字典类型代码获取完整的字典信息
     * <p>
     * 根据字典类型代码查询并返回完整的字典类型对象，包括该类型的所有字典值信息。
     * 注意：仅能获取到 {@link DictProvider#isStoreDictType()} 返回 {@code true} 的字典类型信息。
     * 当 {@link DictProvider#isStoreDictType()} 返回 {@code false} 时，对应的 {@link DictProvider}
     * 提供的字典类型信息对象将不会被存储，因此当前方法无法获得该字典类型信息。
     * </p>
     *
     * @param type 字典类型代码，标识要查询的字典类型
     * @return 完整的字典类型对象，包含字典类型代码和字典值列表；如果不存在则返回 {@code null}
     */
    DictType getDictType(String type);

    /**
     * 通过字典类型代码和字典值获取字典文本信息
     * <p>
     * 根据字典类型代码和字典值查询对应的字典文本（标题）。
     * 这是数据字典系统最常用的方法，用于将字典值转换为可读的文本显示。
     * 如果本地存储中不存在该字典值，可能会通过 {@link RemoteDict} 尝试远程获取。
     * </p>
     *
     * @param type  字典类型代码，标识字典所属的类型
     * @param value 字典值，需要查询文本的具体值
     * @return 字典文本（标题）；如果不存在则返回 {@code null}
     */
    String getDictText(String type, String value);

    /**
     * 通过字典类型代码和字典值获取字典父级值
     * <p>
     * 根据字典类型代码和字典值查询对应的父级字典值。
     * 该方法主要用于树形结构字典，用于获取字典值的父级关系信息。
     * 如果字典值没有父级值或不存在，则返回 {@code null}。
     * </p>
     *
     * @param type  字典类型代码，标识字典所属的类型
     * @param value 字典值，需要查询父级值的具体值
     * @return 字典父级值；如果不存在或没有父级则返回 {@code null}
     * @since 1.4.6
     */
    String getDictParentValue(String type, String value);
}
