package test.application.common;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.houkunlin.system.dict.starter.cache.DictCacheCustomizer;
import org.springframework.stereotype.Component;

/**
 * @author HouKunLin
 */
@Component
public class MyDictCacheCustomizer implements DictCacheCustomizer {
    @Override
    public void customize(final Caffeine<Object, Object> caffeine) {
        System.out.println(caffeine);
    }
}
