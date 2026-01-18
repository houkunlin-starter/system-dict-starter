package com.houkunlin.dict.provider;

import com.houkunlin.dict.SystemDictScan;
import com.houkunlin.dict.bean.DictType;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 系统字典提供者实现类
 * <p>
 * {@link SystemDictScan} 注解扫描到的系统字典都会写入到该对象中进行本地缓存。
 * 该类实现了 {@link DictProvider} 接口，负责管理系统自动生成的字典数据，
 * 如枚举类型转换的字典等。系统字典提供者会存储完整的字典类型信息，
 * 以便为前端页面提供完整的字典值列表数据。
 * </p>
 *
 * @author HouKunLin
 * @since 1.0.0
 */
@Getter
public class SystemDictProvider implements DictProvider {
    private static final Logger logger = LoggerFactory.getLogger(SystemDictProvider.class);
    /**
     * 系统字典缓存
     * <p>
     * 用于缓存所有系统扫描到的字典类型信息，键为字典类型代码，值为对应的字典类型对象。
     * 该缓存确保系统字典数据在内存中快速访问，避免重复扫描和解析。
     * </p>
     */
    private final Map<String, DictType> cache = new HashMap<>();

    /**
     * 判断是否存储完整的字典类型信息
     * <p>
     * 系统字典的字典类型信息（含字典值列表）必须存储到缓存中，
     * 否则无法给前端页面提供完整可用的字典值列表数据。
     * 该方法始终返回 {@code true}，表示系统字典提供者会存储完整的字典类型对象。
     * </p>
     *
     * @return 总是返回 {@code true}
     * @see DictProvider#isStoreDictType()
     */
    @Override
    public boolean isStoreDictType() {
        return true;
    }

    /**
     * 获取一个字典类型对象
     * <p>
     * 根据字典类型代码从缓存中获取对应的字典类型对象。
     * 如果缓存中不存在该字典类型，则使用提供的创建函数创建一个新的字典类型对象，
     * 并将其添加到缓存中，然后返回该对象。
     * </p>
     *
     * @param dictType 字典类型代码
     * @param creator  创建一个新的字典类型对象的函数，当缓存中不存在该字典类型时调用
     * @return 字典类型对象，如果缓存中存在则直接返回，否则创建新对象后返回
     * @since 1.4.7
     */
    public DictType getDict(final String dictType, final Supplier<DictType> creator) {
        return cache.computeIfAbsent(dictType, s -> creator.get());
    }

    /**
     * 获取字典类型迭代器
     * <p>
     * 返回缓存中所有字典类型对象的迭代器。
     * 在调试模式下，会记录当前系统字典类型的数量。
     * 该方法用于 {@link DictRegistrar} 注册字典数据时遍历所有系统字典。
     * </p>
     *
     * @return 字典类型对象的迭代器
     */
    @Override
    public Iterator<DictType> dictTypeIterator() {
        final Collection<DictType> values = cache.values();
        if (logger.isDebugEnabled()) {
            logger.debug("当前系统共有 {} 个系统字典类型信息", values.size());
        }
        return values.iterator();
    }
}
