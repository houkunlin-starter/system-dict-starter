package com.houkunlin.dict.store;

import com.houkunlin.dict.DictUtil;
import com.houkunlin.dict.bean.DictType;
import com.houkunlin.dict.bean.DictValue;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.RedisHashCommands;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.ObjectUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Redis 字典存储实现类
 * <p>
 * 当系统中存在 Redis 环境时，使用 Redis 来存储系统字典信息。
 * 该类实现了 {@link DictStore} 接口和 {@link InitializingBean} 接口，
 * 支持字典数据的存储、删除和查询操作。
 * 该实现提供了批量写入优化，适用于大规模字典数据的场景。
 * </p>
 *
 * @author HouKunLin
 * @since 1.0.0
 */
@Data
@RequiredArgsConstructor
public class RedisDictStore implements DictStore, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(RedisDictStore.class);
    private final RedisTemplate<String, DictType> redisTemplate;
    private final RemoteDict remoteDict;
    /**
     * Redis 批量数据写入时的批次大小
     * <p>
     * 每达到该数量的字典数据时提交一次批量操作，默认值为 1000。
     * 该参数用于控制 Redis 批量写入的大小，优化网络传输和 Redis 处理性能。
     * 对于大规模字典数据，合理设置该参数可以显著提升写入性能。
     * </p>
     */
    private int batchSize = 1000;

    /**
     * 存储一个完整的数据字典信息
     * <p>
     * 将包含字典类型和所有字典值的完整字典对象存储到 Redis 中。
     * 如果字典值列表为 {@code null}，则会调用 {@link #removeDictType(String)} 方法删除该字典类型。
     * 否则将字典类型对象存储到 Redis 中，键为通过 {@link DictUtil#dictKey(String)} 生成的键。
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
            redisTemplate.opsForValue().set(DictUtil.dictKey(dictType.getType()), dictType);
        }
    }

    /**
     * 存储一个完整的系统字典信息
     * <p>
     * 专门为存储系统字典定义的方法，系统字典通常指由系统自动生成的字典类型，
     * 如枚举转换的字典等。将系统字典类型对象存储到 Redis 中，
     * 键为通过 {@link DictUtil#dictSystemKey(String)} 生成的键。
     * 如果字典值列表为 {@code null}，则从 Redis 中删除该系统字典类型。
     * </p>
     *
     * @param dictType 系统字典对象，包含字典类型代码和字典值列表
     */
    @Override
    public void storeSystemDict(DictType dictType) {
        final List<DictValue> children = dictType.getChildren();
        if (children == null) {
            redisTemplate.delete(DictUtil.dictSystemKey(dictType.getType()));
        } else {
            redisTemplate.opsForValue().set(DictUtil.dictSystemKey(dictType.getType()), dictType);
        }
    }

    /**
     * 存储字典值迭代器数据
     * <p>
     * 通过迭代器批量存储字典值对象到 Redis 中。
     * 遍历迭代器中的每个字典值对象，根据字典文本是否为空决定是存储还是删除：
     * - 如果字典文本为 {@code null}，则从 Redis Hash 中删除该字典值
     * - 如果字典文本不为 {@code null}，则存储到 Redis Hash 中
     * 同时处理字典值的父级关系信息。
     * </p>
     *
     * @param iterator 字典值迭代器，用于遍历多个字典值对象
     */
    @Override
    public void store(final Iterator<DictValue> iterator) {
        HashOperations<String, String, String> opsedForHash = redisTemplate.opsForHash();

        iterator.forEachRemaining(valueVo -> {
            String dictKeyHash = DictUtil.dictKeyHash(valueVo);
            String value = ObjectUtils.getDisplayString(valueVo.getValue());
            final String title = valueVo.getTitle();
            if (title == null) {
                opsedForHash.delete(dictKeyHash, value);
                if (logger.isDebugEnabled()) {
                    logger.debug("[removeDictValue] 字典值文本被删除 {}#{}", dictKeyHash, value);
                }
            } else {
                opsedForHash.put(dictKeyHash, value, title);
                // @since 1.4.6 - START
                final String dictParentKeyHash = DictUtil.dictParentKeyHash(valueVo);
                final Object parentValue = valueVo.getParentValue();
                if (parentValue == null) {
                    opsedForHash.delete(dictParentKeyHash, value);
                } else {
                    opsedForHash.put(dictParentKeyHash, value, parentValue.toString());
                }
                // @since 1.4.6 - END
            }
        });
    }

    /**
     * 批量存储字典值迭代器数据
     * <p>
     * 使用 Redis 管道（Pipeline）技术批量存储字典值对象，适用于大规模字典数据的场景。
     * 每达到 {@link #batchSize} 条数据时提交一次管道操作，优化网络传输性能。
     * 遍历迭代器中的每个字典值对象，根据字典文本是否为空决定是存储还是删除：
     * - 如果字典文本为 {@code null}，则从 Redis Hash 中删除该字典值
     * - 如果字典文本不为 {@code null}，则存储到 Redis Hash 中
     * 同时处理字典值的父级关系信息。
     * </p>
     *
     * @param iterator 字典值迭代器，用于遍历多个字典值对象
     */
    @Override
    public void storeBatch(final Iterator<DictValue> iterator) {
        RedisSerializer<String> keySerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
        RedisSerializer<String> hashKeySerializer = (RedisSerializer<String>) redisTemplate.getHashKeySerializer();
        RedisSerializer<String> hashValueSerializer = (RedisSerializer<String>) redisTemplate.getHashValueSerializer();
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            try {
                AtomicInteger index = new AtomicInteger(0);
                RedisHashCommands redisHashCommands = connection.hashCommands();
                iterator.forEachRemaining(valueVo -> {
                    // 每 batchSize 条数据提交一次
                    if (index.getAndIncrement() % batchSize == 0) {
                        if (connection.isPipelined()) {
                            connection.closePipeline();
                        }
                        connection.openPipeline();
                    }
                    final String dictKeyHash = DictUtil.dictKeyHash(valueVo);
                    final String value = ObjectUtils.getDisplayString(valueVo.getValue());
                    final String title = valueVo.getTitle();

                    final byte[] dictKeyHashByte = keySerializer.serialize(dictKeyHash);
                    final byte[] valueByte = hashKeySerializer.serialize(value);

                    if (dictKeyHashByte == null || valueByte == null) {
                        return;
                    }

                    if (title == null) {
                        /// opsedForHash.delete(dictKeyHash, value);
                        // 删除字典值
                        redisHashCommands.hDel(dictKeyHashByte, valueByte);
                        if (logger.isDebugEnabled()) {
                            logger.debug("[removeDictValue] 字典值文本被删除 {}#{}", dictKeyHash, value);
                        }
                    } else {
                        final byte[] titleByte = hashValueSerializer.serialize(title);
                        if (titleByte != null) {
                            /// opsedForHash.put(dictKeyHash, value, title);
                            // 存储字典值和文本
                            redisHashCommands.hSet(dictKeyHashByte, valueByte, titleByte);
                        }
                        final String dictParentKeyHash = DictUtil.dictParentKeyHash(valueVo);
                        final Object parentValue = valueVo.getParentValue();

                        final byte[] dictParentKeyHashByte = keySerializer.serialize(dictParentKeyHash);

                        if (dictParentKeyHashByte == null) {
                            return;
                        }

                        if (parentValue == null) {
                            /// opsedForHash.delete(dictParentKeyHash, value);
                            // 删除父级关系
                            redisHashCommands.hDel(dictParentKeyHashByte, valueByte);
                        } else {
                            final byte[] parentValueByte = hashValueSerializer.serialize(parentValue.toString());
                            if (parentValueByte != null) {
                                /// opsedForHash.put(dictParentKeyHash, value, parentValue.toString());
                                // 存储父级关系
                                redisHashCommands.hSet(dictParentKeyHashByte, valueByte, parentValueByte);
                            }
                        }
                    }
                });
                if (connection.isPipelined()) {
                    connection.closePipeline();
                }
                connection.close();
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error("Redis 批量写入数据字典信息错误", e);
                }
            }
            return null;
        });
    }

    /**
     * 删除一个字典类型及其所有字典值
     * <p>
     * 从 Redis 中删除指定的字典类型及其所有相关的字典值数据。
     * 该方法执行以下操作：
     * 1. 删除字典类型对象（通过 {@link DictUtil#dictKey(String)} 生成的键）
     * 2. 删除该字典类型对应的所有字典值文本（通过 {@link DictUtil#dictKeyHash(String)} 生成的键）
     * 该方法用于清理不再需要的字典数据。
     * </p>
     *
     * @param dictType 字典类型代码，标识要删除的字典类型
     */
    @Override
    public void removeDictType(final String dictType) {
        String dictKeyType = DictUtil.dictKey(dictType);
        redisTemplate.delete(dictKeyType);
        if (logger.isDebugEnabled()) {
            logger.debug("[removeDictType] 字典类型被删除 {}", dictKeyType);
        }
        final String dictKeyHashValue = DictUtil.dictKeyHash(dictType);
        redisTemplate.delete(dictKeyHashValue);
        if (logger.isDebugEnabled()) {
            logger.debug("[removeDictType] 字典值文本被删除 {}", dictKeyHashValue);
        }
    }

    /**
     * 获取所有字典类型代码列表
     * <p>
     * 通过 Redis 的 keys 命令查询所有以 {@link DictUtil#TYPE_PREFIX} 为前缀的键，
     * 然后从这些键中提取出字典类型代码。
     * 该方法返回存储系统中所有普通字典类型的代码集合。
     * </p>
     *
     * @return 字典类型代码集合，包含所有已存储的普通字典类型代码
     */
    @Override
    public Set<String> dictTypeKeys() {
        final Set<String> keys = redisTemplate.keys(DictUtil.TYPE_PREFIX.concat("*"));
        assert keys != null;
        final int length = DictUtil.TYPE_PREFIX.length();
        return keys.stream().map(key -> key.substring(length)).collect(Collectors.toSet());
    }

    /**
     * 获取系统字典类型代码列表
     * <p>
     * 通过 Redis 的 keys 命令查询所有以 {@link DictUtil#TYPE_SYSTEM_PREFIX} 为前缀的键，
     * 然后从这些键中提取出系统字典类型代码。
     * 该方法返回存储系统中所有系统字典类型的代码集合。
     * 系统字典通常指由系统自动生成的字典类型，如枚举转换的字典等。
     * </p>
     *
     * @return 系统字典类型代码集合，仅包含系统字典类型代码
     */
    @Override
    public Set<String> systemDictTypeKeys() {
        final Set<String> keys = redisTemplate.keys(DictUtil.TYPE_SYSTEM_PREFIX.concat("*"));
        assert keys != null;
        final int length = DictUtil.TYPE_SYSTEM_PREFIX.length();
        return keys.stream().map(key -> key.substring(length)).collect(Collectors.toSet());
    }

    /**
     * 通过字典类型代码获取完整的字典信息
     * <p>
     * 根据字典类型代码从 Redis 中查询完整的字典类型对象。
     * 首先尝试从 Redis 中获取，如果 Redis 中不存在，
     * 则通过 {@link #remoteDict} 接口尝试从远程获取字典信息。
     * 这种设计允许系统处理动态字典数据，即使它们不在 Redis 中预先存储。
     * </p>
     *
     * @param type 字典类型代码，标识要查询的字典类型
     * @return 完整的字典类型对象，包含字典类型代码和字典值列表；如果不存在则返回 {@code null}
     */
    @Override
    public DictType getDictType(final String type) {
        if (type == null) {
            return null;
        }
        final DictType o = redisTemplate.opsForValue().get(DictUtil.dictKey(type));
        if (o != null) {
            return o;
        }
        // 例如 Redis 中不存在这个字典，说明可能是一个用户字典，此时需要调用系统模块服务来获取用户字典
        return remoteDict.getDictType(type);
    }

    /**
     * 通过字典类型代码和字典值获取字典文本信息
     * <p>
     * 根据字典类型代码和字典值从 Redis Hash 中查询对应的字典文本（标题）。
     * 首先尝试从 Redis 中获取，如果 Redis 中不存在，
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
        if (type == null || value == null) {
            return null;
        }
        final String o = redisTemplate.<String, String>opsForHash().get(DictUtil.dictKeyHash(type), value);
        if (o != null) {
            return o;
        }
        // 例如 Redis 中不存在这个字典，说明可能是一个用户字典，此时需要调用系统模块服务来获取用户字典
        return remoteDict.getDictText(type, value);
    }

    /**
     * 通过字典类型代码和字典值获取字典父级值
     * <p>
     * 根据字典类型代码和字典值从 Redis Hash 中查询对应的父级字典值。
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
        if (type == null || value == null) {
            return null;
        }
        return redisTemplate.<String, String>opsForHash().get(DictUtil.dictParentKeyHash(type), value);
    }

    /**
     * Bean 初始化后执行的方法
     * <p>
     * 实现 {@link InitializingBean} 接口的方法，在 Bean 初始化完成后调用。
     * 该方法主要用于记录 Redis 字典存储的使用情况，在调试模式下输出当前使用的存储实现类。
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
