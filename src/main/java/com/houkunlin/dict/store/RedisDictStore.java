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
 * 当存在 Redis 环境时使用 Redis 来存储系统字典信息，否则使用本地存储。
 *
 * @author HouKunLin
 */
@Data
@RequiredArgsConstructor
public class RedisDictStore implements DictStore, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(RedisDictStore.class);
    private final RedisTemplate<String, DictType> redisTemplate;
    private final RemoteDict remoteDict;
    /**
     * Redis 批量数据写入时，每 1000 条字典数据提交一次
     */
    private int batchSize = 1000;

    @Override
    public void store(final DictType dictType) {
        final List<DictValue> children = dictType.getChildren();
        if (children == null) {
            removeDictType(dictType.getType());
        } else {
            redisTemplate.opsForValue().set(DictUtil.dictKey(dictType.getType()), dictType);
        }
    }

    @Override
    public void storeSystemDict(DictType dictType) {
        final List<DictValue> children = dictType.getChildren();
        if (children == null) {
            redisTemplate.delete(DictUtil.dictSystemKey(dictType.getType()));
        } else {
            redisTemplate.opsForValue().set(DictUtil.dictSystemKey(dictType.getType()), dictType);
        }
    }

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
                    // 1000 条数据提交一次
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
                        // opsedForHash.delete(dictKeyHash, value);
                        redisHashCommands.hDel(dictKeyHashByte, valueByte);
                        if (logger.isDebugEnabled()) {
                            logger.debug("[removeDictValue] 字典值文本被删除 {}#{}", dictKeyHash, value);
                        }
                    } else {
                        final byte[] titleByte = hashValueSerializer.serialize(title);
                        if (titleByte != null) {
                            // opsedForHash.put(dictKeyHash, value, title);
                            redisHashCommands.hSet(dictKeyHashByte, valueByte, titleByte);
                        }
                        final String dictParentKeyHash = DictUtil.dictParentKeyHash(valueVo);
                        final Object parentValue = valueVo.getParentValue();

                        final byte[] dictParentKeyHashByte = keySerializer.serialize(dictParentKeyHash);

                        if (dictParentKeyHashByte == null) {
                            return;
                        }

                        if (parentValue == null) {
                            // opsedForHash.delete(dictParentKeyHash, value);
                            redisHashCommands.hDel(dictParentKeyHashByte, valueByte);
                        } else {
                            final byte[] parentValueByte = hashValueSerializer.serialize(parentValue.toString());
                            if (parentValueByte != null) {
                                // opsedForHash.put(dictParentKeyHash, value, parentValue.toString());
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

    @Override
    public Set<String> dictTypeKeys() {
        final Set<String> keys = redisTemplate.keys(DictUtil.TYPE_PREFIX.concat("*"));
        assert keys != null;
        final int length = DictUtil.TYPE_PREFIX.length();
        return keys.stream().map(key -> key.substring(length)).collect(Collectors.toSet());
    }

    @Override
    public Set<String> systemDictTypeKeys() {
        final Set<String> keys = redisTemplate.keys(DictUtil.TYPE_SYSTEM_PREFIX.concat("*"));
        assert keys != null;
        final int length = DictUtil.TYPE_SYSTEM_PREFIX.length();
        return keys.stream().map(key -> key.substring(length)).collect(Collectors.toSet());
    }

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

    @Override
    public String getDictParentValue(final String type, final String value) {
        if (type == null || value == null) {
            return null;
        }
        return redisTemplate.<String, String>opsForHash().get(DictUtil.dictParentKeyHash(type), value);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("使用 {} 存储数据字典信息", getClass().getName());
        }
    }
}
