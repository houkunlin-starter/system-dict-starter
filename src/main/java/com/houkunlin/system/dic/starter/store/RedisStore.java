package com.houkunlin.system.dic.starter.store;

import com.houkunlin.system.dic.starter.DicUtil;
import com.houkunlin.system.dic.starter.bean.DicTypeVo;
import com.houkunlin.system.dic.starter.bean.DicValueVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Iterator;

/**
 * @author HouKunLin
 */
@ConditionalOnClass(RedisTemplate.class)
@Component
public class RedisStore implements DicStore {
    private static final Logger logger = LoggerFactory.getLogger(RedisStore.class);
    public final RedisTemplate<Object, Object> redisTemplate;
    private final RemoteDic remoteDic;

    public RedisStore(final RedisTemplate<Object, Object> redisTemplate, final RemoteDic remoteDic) {
        this.redisTemplate = redisTemplate;
        this.remoteDic = remoteDic;
    }

    @Override
    public void store(final DicTypeVo dicType) {
        redisTemplate.opsForValue().set(DicUtil.dicKey(dicType.getType()), dicType);
    }

    @Override
    public void store(final Iterator<DicValueVo<? extends Serializable>> iterator) {
        iterator.forEachRemaining(valueVo -> redisTemplate.opsForValue().set(DicUtil.dicKey(valueVo), valueVo.getTitle()));
    }

    @Override
    public DicTypeVo getDicType(final String type) {
        if (type == null) {
            return null;
        }
        final Object o = redisTemplate.opsForValue().get(DicUtil.dicKey(type));
        if (o != null) {
            return (DicTypeVo) o;
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
