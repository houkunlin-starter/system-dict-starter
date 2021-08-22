package test.application.server.redis;

import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.store.RedisDictStore;
import com.houkunlin.system.dict.starter.store.RemoteDict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author HouKunLin
 */
@Configuration
public class DictConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DictConfiguration.class);

    @Bean
    public RedisDictStore redisDicStore(final RedisTemplate<Object, Object> redisTemplate, final RemoteDict remoteDic) {
        logger.debug("使用自定义的 RedisDicStore 存储数据字典信息");
        return new RedisDictStore(redisTemplate, remoteDic);
    }

    @Bean
    public RemoteDict remoteDic() {
        logger.debug("提供一个空的 RemoteDic 对象");
        return new RemoteDict() {
            @Override
            public DictTypeVo getDicType(final String type) {
                return null;
            }

            @Override
            public String getDicValueTitle(final String type, final String value) {
                return null;
            }
        };
    }
}
