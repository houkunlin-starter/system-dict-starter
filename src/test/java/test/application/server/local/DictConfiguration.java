package test.application.server.local;

import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.store.LocalDictStore;
import com.houkunlin.system.dict.starter.store.RemoteDict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author HouKunLin
 */
@Configuration
public class DictConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DictConfiguration.class);

    @Bean
    public LocalDictStore localDicStore(final RemoteDict remoteDic) {
        logger.debug("使用自定义的 LocalDicStore 存储数据字典信息");
        return new LocalDictStore(remoteDic);
    }

    @Bean
    public RemoteDict remoteDic() {
        logger.debug("提供一个空的 RemoteDic 对象");
        return new RemoteDict() {
            @Override
            public DictTypeVo getDictType(final String type) {
                return null;
            }

            @Override
            public String getDictText(final String type, final String value) {
                return null;
            }
        };
    }
}
