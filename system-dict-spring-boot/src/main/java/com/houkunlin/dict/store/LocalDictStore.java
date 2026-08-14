package com.houkunlin.dict.store;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.DictUtil;
import com.houkunlin.dict.bean.DictType;
import com.houkunlin.dict.bean.DictValue;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地字典存储实现类
 * <p>
 * 使用本地 {@link ConcurrentHashMap} 来存储系统字典信息，提供内存级别的快速访问。
 * 当系统中不存在 Redis 环境时，默认使用该存储方式。
 * 该类实现了 {@link DictStore} 接口和 {@link InitializingBean} 接口，
 * 支持字典数据的存储、查询和删除操作，同时与 {@link RemoteDict} 配合实现远程字典获取。
 * </p>
 *
 * @author HouKunLin
 * @since 1.0.0
 */
@AllArgsConstructor
public class LocalDictStore implements DictStore, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(LocalDictStore.class);
    /**
     * 普通字典类型缓存
     * <p>
     * 存储所有普通字典类型对象，键为字典类型代码，值为完整的字典类型对象。
     * 该缓存用于快速查询字典类型信息和字典值列表。
     * </p>
     */
    private static final ConcurrentHashMap<String, DictType> CACHE_TYPE = new ConcurrentHashMap<>();
    /**
     * 系统字典类型缓存
     * <p>
     * 专门存储系统字典类型对象，键为字典类型代码，值为完整的系统字典类型对象。
     * 系统字典通常指由系统自动生成的字典类型，如枚举转换的字典等。
     * </p>
     */
    private static final ConcurrentHashMap<String, DictType> CACHE_SYSTEM_TYPE = new ConcurrentHashMap<>();
    /**
     * 字典文本缓存
     * <p>
     * 存储字典值对应的文本信息，键为字典键（由字典类型代码和字典值组成），值为字典文本。
     * 该缓存用于快速将字典值转换为可读的文本显示。
     * </p>
     */
    private static final ConcurrentHashMap<String, String> CACHE_TEXT = new ConcurrentHashMap<>();
    /**
     * 远程字典获取接口
     * <p>
     * 当本地缓存中不存在所需的字典数据时，通过该接口尝试从远程获取字典信息。
     * </p>
     */
    private final RemoteDict remoteDict;

    /**
     * 存储一个完整的数据字典信息
     * <p>
     * 将包含字典类型和所有字典值的完整字典对象存储到本地缓存中。
     * 如果字典值列表为 {@code null}，则会调用 {@link #removeDictType(String)} 方法删除该字典类型。
     * 否则将字典类型对象存储到 {@link #CACHE_TYPE} 缓存中。
     * </p>
     *
     * @param dictType 数据字典对象，包含字典类型代码和字典值列表
     */
    @Override
    public void store(final DictType dictType) {
        final List<DictValue> children = dictType.getChildren();
        if (children == null) {
            removeDictType(dictType.getType());
        } else {
            CACHE_TYPE.put(dictType.getType(), dictType);
        }
    }

    /**
     * 存储一个完整的系统字典信息
     * <p>
     * 专门为存储系统字典定义的方法，系统字典指实现 {@link DictEnum} 接口的枚举类字典，
     * 如枚举转换的字典等。将系统字典类型对象存储到 {@link #CACHE_SYSTEM_TYPE} 缓存中。
     * 如果字典值列表为 {@code null}，则从缓存中删除该字典类型。
     * </p>
     *
     * @param dictType 系统字典对象，包含字典类型代码和字典值列表
     */
    @Override
    public void storeSystemDict(DictType dictType) {
        final List<DictValue> children = dictType.getChildren();
        if (children == null) {
            CACHE_SYSTEM_TYPE.remove(dictType.getType());
        } else {
            CACHE_SYSTEM_TYPE.put(dictType.getType(), dictType);
        }
    }

    /**
     * 存储字典值迭代器数据
     * <p>
     * 通过迭代器批量存储字典值对象到本地缓存中。
     * 遍历迭代器中的每个字典值对象，根据字典文本是否为空决定是存储还是删除：
     * - 如果字典文本为 {@code null}，则从 {@link #CACHE_TEXT} 缓存中删除该字典值
     * - 如果字典文本不为 {@code null}，则存储到 {@link #CACHE_TEXT} 缓存中
     * 同时处理字典值的父级关系信息。
     * </p>
     *
     * @param iterator 字典值迭代器，用于遍历多个字典值对象
     */
    @Override
    public void store(final Iterator<DictValue> iterator) {
        iterator.forEachRemaining(valueVo -> {
            final String dictKey = DictUtil.dictKey(valueVo);
            final String title = valueVo.getTitle();
            if (title == null) {
                CACHE_TEXT.remove(dictKey);
                if (logger.isDebugEnabled()) {
                    logger.debug("[removeDictValue] 字典值文本被删除 {}", dictKey);
                }
            } else {
                CACHE_TEXT.put(dictKey, title);
                // @since 1.4.6 - START
                final String dictParentKey = DictUtil.dictParentKey(valueVo);
                final Object parentValue = valueVo.getParentValue();
                if (parentValue == null) {
                    CACHE_TEXT.remove(dictParentKey);
                } else {
                    CACHE_TEXT.put(dictParentKey, parentValue.toString());
                }
                // @since 1.4.6 - END
            }
        });
    }

    /**
     * 删除一个字典类型及其所有字典值
     * <p>
     * 从本地缓存中删除指定的字典类型及其所有相关的字典值数据。
     * 操作包括：
     * 1. 从 {@link #CACHE_TYPE} 缓存中删除字典类型对象
     * 2. 从 {@link #CACHE_TEXT} 缓存中删除所有以该字典类型为前缀的字典值文本
     * 该方法用于清理不再需要的字典数据。
     * </p>
     *
     * @param dictType 字典类型代码，标识要删除的字典类型
     */
    @Override
    public void removeDictType(final String dictType) {
        CACHE_TYPE.remove(dictType);
        if (logger.isDebugEnabled()) {
            logger.debug("[removeDictType] 字典类型被删除 {}", dictType);
        }
        final String prefix = DictUtil.VALUE_PREFIX.concat(dictType);
        CACHE_TEXT.entrySet().removeIf(entry -> {
            final String entryKey = entry.getKey();
            if (entryKey != null && entryKey.startsWith(prefix)) {
                logger.debug("[removeDictType] 字典值文本被删除 {}", entryKey);
                return true;
            }
            return false;
        });
    }

    /**
     * 获取所有字典类型代码列表
     * <p>
     * 返回本地缓存中所有普通字典类型的代码集合。
     * 该方法返回 {@link #CACHE_TYPE} 缓存的所有键，即所有已存储的普通字典类型代码。
     * </p>
     *
     * @return 字典类型代码集合，包含所有已存储的普通字典类型代码
     */
    @Override
    public Set<String> dictTypeKeys() {
        return CACHE_TYPE.keySet();
    }

    /**
     * 获取系统字典类型代码列表
     * <p>
     * 返回本地缓存中所有系统字典类型的代码集合。
     * 该方法返回 {@link #CACHE_SYSTEM_TYPE} 缓存的所有键，即所有已存储的系统字典类型代码。
     * 系统字典通常指由系统自动生成的字典类型，如枚举转换的字典等。
     * </p>
     *
     * @return 系统字典类型代码集合，仅包含系统字典类型代码
     */
    @Override
    public Set<String> systemDictTypeKeys() {
        return CACHE_SYSTEM_TYPE.keySet();
    }

    /**
     * 通过字典类型代码获取完整的字典信息
     * <p>
     * 根据字典类型代码从本地缓存中查询完整的字典类型对象。
     * 首先尝试从 {@link #CACHE_TYPE} 缓存中获取，如果缓存中不存在，
     * 则通过 {@link #remoteDict} 接口尝试从远程获取字典信息。
     * </p>
     *
     * @param type 字典类型代码，标识要查询的字典类型
     * @return 完整的字典类型对象，包含字典类型代码和字典值列表；如果不存在则返回 {@code null}
     */
    @Override
    public DictType getDictType(final String type) {
        final DictType typeVo = CACHE_TYPE.get(type);
        if (typeVo != null) {
            return typeVo;
        }
        return remoteDict.getDictType(type);
    }

    /**
     * 通过字典类型代码和字典值获取字典文本信息
     * <p>
     * 根据字典类型代码和字典值从本地缓存中查询对应的字典文本。
     * 首先尝试从 {@link #CACHE_TEXT} 缓存中获取，如果缓存中不存在，
     * 则通过 {@link #remoteDict} 接口尝试从远程获取字典文本信息。
     * 这是数据字典系统最常用的方法，用于将字典值转换为可读的文本显示。
     * </p>
     *
     * @param type  字典类型代码，标识字典所属的类型
     * @param value 字典值，需要查询文本的具体值
     * @return 字典文本（标题）；如果不存在则返回 {@code null}
     */
    @Override
    public String getDictText(final String type, final String value) {
        final String title = CACHE_TEXT.get(DictUtil.dictKey(type, value));
        if (title != null) {
            return title;
        }
        return remoteDict.getDictText(type, value);
    }

    /**
     * 通过字典类型代码和字典值获取字典父级值
     * <p>
     * 根据字典类型代码和字典值从本地缓存中查询对应的父级字典值。
     * 该方法主要用于树形结构字典，用于获取字典值的父级关系信息。
     * 如果字典值没有父级值或不存在，则返回 {@code null}。
     * </p>
     *
     * @param type  字典类型代码，标识字典所属的类型
     * @param value 字典值，需要查询父级值的具体值
     * @return 字典父级值；如果不存在或没有父级则返回 {@code null}
     */
    @Override
    public String getDictParentValue(final String type, final String value) {
        return CACHE_TEXT.get(DictUtil.dictParentKey(type, value));
    }

    /**
     * Bean 初始化后执行的方法
     * <p>
     * 实现 {@link InitializingBean} 接口的方法，在 Bean 初始化完成后调用。
     * 该方法主要用于记录本地字典存储的使用情况，在调试模式下输出当前使用的存储实现类。
     * </p>
     *
     * @throws Exception 初始化过程中可能抛出的异常
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("使用 {} 存储数据字典信息", getClass().getName());
        }
    }
}
