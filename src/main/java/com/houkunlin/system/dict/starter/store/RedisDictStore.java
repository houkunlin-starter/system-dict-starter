package com.houkunlin.system.dict.starter.store;

import com.houkunlin.system.dict.starter.DictUtil;
import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;
import java.util.Iterator;

/**
 * 当存在 Redis 环境时使用 Redis 来存储系统字典信息，否则使用本地存储。
 *
 * @author HouKunLin
 */
public class RedisDictStore implements DictStore {
    private static final Logger logger = LoggerFactory.getLogger(RedisDictStore.class);
    public final RedisTemplate<Object, Object> redisTemplate;
    private final RemoteDict remoteDic;

    public RedisDictStore(final RedisTemplate<Object, Object> redisTemplate, final RemoteDict remoteDic) {
        this.redisTemplate = redisTemplate;
        this.remoteDic = remoteDic;
    }

    @Override
    public void store(final DictTypeVo dictType) {
        redisTemplate.opsForValue().set(DictUtil.dictKey(dictType.getType()), dictType);
    }

    @Override
    public void store(final Iterator<DictValueVo> iterator) {
        iterator.forEachRemaining(valueVo -> redisTemplate.opsForValue().set(DictUtil.dictKey(valueVo), valueVo.getTitle()));
    }

    @Override
    public DictTypeVo getDictType(final String type) {
        if (type == null) {
            return null;
        }
        final Object o = redisTemplate.opsForValue().get(DictUtil.dictKey(type));
        if (o != null) {
            return (DictTypeVo) o;
        }
        // 例如 Redis 中不存在这个字典，说明可能是一个用户字典，此时需要调用系统模块服务来获取用户字典
        return remoteDic.getDictType(type);
    }

    @Override
    public String getDictText(final String type, final String value) {
        if (type == null || value == null) {
            return null;
        }
        final Object o = redisTemplate.opsForValue().get(DictUtil.dictKey(type, value));
        if (o != null) {
            return String.valueOf(o);
        }
        // 例如 Redis 中不存在这个字典，说明可能是一个用户字典，此时需要调用系统模块服务来获取用户字典
        return remoteDic.getDictText(type, value);
    }

    @PostConstruct
    public void post() {
        logger.info("使用 Redis 存储数据字典信息");
    }
}
