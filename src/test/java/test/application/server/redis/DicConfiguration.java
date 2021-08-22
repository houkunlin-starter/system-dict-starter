package test.application.server.redis;

import com.houkunlin.system.dict.starter.bean.DicTypeVo;
import com.houkunlin.system.dict.starter.store.RedisDicStore;
import com.houkunlin.system.dict.starter.store.RemoteDic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author HouKunLin
 */
@Configuration
public class DicConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DicConfiguration.class);

    @Bean
    public RedisDicStore redisDicStore(final RedisTemplate<Object, Object> redisTemplate, final RemoteDic remoteDic) {
        logger.debug("使用自定义的 RedisDicStore 存储数据字典信息");
        return new RedisDicStore(redisTemplate, remoteDic);
    }

    @Bean
    public RemoteDic remoteDic() {
        logger.debug("提供一个空的 RemoteDic 对象");
        return new RemoteDic() {
            @Override
            public DicTypeVo getDicType(final String type) {
                return null;
            }

            @Override
            public String getDicValueTitle(final String type, final String value) {
                return null;
            }
        };
    }
}
