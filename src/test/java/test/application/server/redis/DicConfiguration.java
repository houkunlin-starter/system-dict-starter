package test.application.server.redis;

import com.houkunlin.system.dic.starter.store.DicStore;
import com.houkunlin.system.dic.starter.store.RedisDicStore;
import com.houkunlin.system.dic.starter.store.RemoteDic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author HouKunLin
 */
@Configuration
public class DicConfiguration {
    @Bean
    public DicStore dicStore(final RedisTemplate<Object, Object> redisTemplate, final RemoteDic remoteDic) {
        return new RedisDicStore(redisTemplate, remoteDic);
    }
}
