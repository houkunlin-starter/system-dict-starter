package test.application.server.local;

import com.houkunlin.system.dic.starter.store.DicStore;
import com.houkunlin.system.dic.starter.store.LocalDicStore;
import com.houkunlin.system.dic.starter.store.RemoteDic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author HouKunLin
 */
@Configuration
public class DicConfiguration {
    @Bean
    public DicStore dicStore(final RemoteDic remoteDic) {
        return new LocalDicStore(remoteDic);
    }
}
