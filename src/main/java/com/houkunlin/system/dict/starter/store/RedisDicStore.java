package com.houkunlin.system.dict.starter.store;

import com.houkunlin.system.dict.starter.DicUtil;
import com.houkunlin.system.dict.starter.bean.DicTypeVo;
import com.houkunlin.system.dict.starter.bean.DicValueVo;
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
public class RedisDicStore implements DicStore<Object> {
    private static final Logger logger = LoggerFactory.getLogger(RedisDicStore.class);
    public final RedisTemplate<Object, Object> redisTemplate;
    private final RemoteDic<Object> remoteDic;

    public RedisDicStore(final RedisTemplate<Object, Object> redisTemplate, final RemoteDic remoteDic) {
        this.redisTemplate = redisTemplate;
        this.remoteDic = remoteDic;
    }

    @Override
    public void store(final DicTypeVo<Object> dicType) {
        redisTemplate.opsForValue().set(DicUtil.dicKey(dicType.getType()), dicType);
    }

    @Override
    public void store(final Iterator<DicValueVo<Object>> iterator) {
        iterator.forEachRemaining(valueVo -> redisTemplate.opsForValue().set(DicUtil.dicKey(valueVo), valueVo.getTitle()));
    }

    @Override
    public DicTypeVo<Object> getDicType(final String type) {
        if (type == null) {
            return null;
        }
        final Object o = redisTemplate.opsForValue().get(DicUtil.dicKey(type));
        if (o != null) {
            return (DicTypeVo<Object>) o;
        }
        // 例如 Redis 中不存在这个字典，说明可能是一个用户字典，此时需要调用系统模块服务来获取用户字典
        return remoteDic.getDicType(type);
    }

    @Override
    public String getDicValueTitle(final String type, final String value) {
        if (type == null || value == null) {
            return null;
        }
        final Object o = redisTemplate.opsForValue().get(DicUtil.dicKey(type, value));
        if (o != null) {
            return String.valueOf(o);
        }
        // 例如 Redis 中不存在这个字典，说明可能是一个用户字典，此时需要调用系统模块服务来获取用户字典
        return remoteDic.getDicValueTitle(type, value);
    }

    @PostConstruct
    public void post() {
        logger.info("使用 Redis 存储数据字典信息");
    }
}
