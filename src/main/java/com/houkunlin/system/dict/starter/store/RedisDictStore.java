package com.houkunlin.system.dict.starter.store;

import com.houkunlin.system.dict.starter.DictUtil;
import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 当存在 Redis 环境时使用 Redis 来存储系统字典信息，否则使用本地存储。
 *
 * @author HouKunLin
 */
@AllArgsConstructor
public class RedisDictStore implements DictStore, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(RedisDictStore.class);
    public final RedisTemplate<String, DictTypeVo> dictTypeRedisTemplate;
    public final RedisTemplate<String, String> dictValueRedisTemplate;
    private final RemoteDict remoteDict;

    @Override
    public void store(final DictTypeVo dictType) {
        final ValueOperations<String, DictTypeVo> opsForValue = dictTypeRedisTemplate.opsForValue();
        final List<DictValueVo> children = dictType.getChildren();
        final String dictKey = DictUtil.dictKey(dictType.getType());
        if (children == null) {
            opsForValue.getOperations().delete(dictKey);
        } else {
            opsForValue.set(dictKey, dictType);
        }
    }

    @Override
    public void store(final Iterator<DictValueVo> iterator) {
        final ValueOperations<String, String> opsForValue = dictValueRedisTemplate.opsForValue();
        final RedisOperations<String, String> operations = opsForValue.getOperations();
        iterator.forEachRemaining(valueVo -> {
            final String dictKey = DictUtil.dictKey(valueVo);
            final String title = valueVo.getTitle();
            if (title == null) {
                operations.delete(dictKey);
            } else {
                opsForValue.set(dictKey, title);
            }
        });
    }

    @Override
    public Set<String> dictTypeKeys() {
        final Set<String> keys = dictTypeRedisTemplate.keys(DictUtil.TYPE_PREFIX.concat("*"));
        assert keys != null;
        final int length = DictUtil.TYPE_PREFIX.length();
        return keys.stream().map(key -> key.substring(length)).collect(Collectors.toSet());
    }

    @Override
    public DictTypeVo getDictType(final String type) {
        if (type == null) {
            return null;
        }
        final DictTypeVo o = dictTypeRedisTemplate.opsForValue().get(DictUtil.dictKey(type));
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
        final String o = dictValueRedisTemplate.opsForValue().get(DictUtil.dictKey(type, value));
        if (o != null) {
            return o;
        }
        // 例如 Redis 中不存在这个字典，说明可能是一个用户字典，此时需要调用系统模块服务来获取用户字典
        return remoteDict.getDictText(type, value);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("使用 {} 存储数据字典信息", getClass().getName());
        }
    }
}
