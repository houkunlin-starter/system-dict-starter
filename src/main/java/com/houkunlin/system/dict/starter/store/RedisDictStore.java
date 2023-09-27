package com.houkunlin.system.dict.starter.store;

import com.houkunlin.system.dict.starter.DictUtil;
import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 当存在 Redis 环境时使用 Redis 来存储系统字典信息，否则使用本地存储。
 *
 * @author HouKunLin
 */
@RequiredArgsConstructor
public class RedisDictStore implements DictStore, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(RedisDictStore.class);
    private final RedisTemplate<String, DictTypeVo> redisTemplate;
    private final RemoteDict remoteDict;

    @Override
    public void store(final DictTypeVo dictType) {
        final List<DictValueVo> children = dictType.getChildren();
        if (children == null) {
            removeDictType(dictType.getType());
        } else {
            redisTemplate.opsForValue().set(DictUtil.dictKey(dictType.getType()), dictType);
        }
    }

    @Override
    public void storeSystemDict(DictTypeVo dictType) {
        final List<DictValueVo> children = dictType.getChildren();
        if (children == null) {
            redisTemplate.delete(DictUtil.dictSystemKey(dictType.getType()));
        } else {
            redisTemplate.opsForValue().set(DictUtil.dictSystemKey(dictType.getType()), dictType);
        }
    }

    @Override
    public void store(final Iterator<DictValueVo> iterator) {
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
    public DictTypeVo getDictType(final String type) {
        if (type == null) {
            return null;
        }
        final DictTypeVo o = redisTemplate.opsForValue().get(DictUtil.dictKey(type));
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
