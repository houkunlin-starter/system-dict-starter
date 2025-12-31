package com.houkunlin.system.dict.starter;

import com.houkunlin.system.dict.starter.cache.DictCacheFactory;
import com.houkunlin.system.dict.starter.jackson.DictJacksonModule;
import com.houkunlin.system.dict.starter.properties.DictProperties;
import com.houkunlin.system.dict.starter.store.DictStore;
import com.houkunlin.system.dict.starter.store.RemoteDict;
import com.houkunlin.system.dict.starter.store.RemoteDictDefaultImpl;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * 自动扫描配置系统注解所需要的Bean对象
 *
 * @author HouKunLin
 */
@Getter
@Configuration(proxyBeanMethods = false)
@Import(DictProperties.class)
public class SystemDictAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(SystemDictAutoConfiguration.class);
    private static final String WARNING_MESSAGE = "DictProperties 未找到，请在启动类添加 @SystemDictScan 注解启用相关服务";
    private static DictProperties properties;
    private static ApplicationContext applicationContext;

    @Autowired
    public void setProperties(DictProperties properties) {
        SystemDictAutoConfiguration.properties = properties;
        DictUtil.initPrefix(properties.getStoreKey());
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        SystemDictAutoConfiguration.applicationContext = applicationContext;
    }

    public static boolean isRawValue() {
        if (properties == null) {
            logger.warn(WARNING_MESSAGE);
            return false;
        }
        return properties.isRawValue();
    }

    public static boolean isTextValueDefaultNull() {
        if (properties == null) {
            logger.warn(WARNING_MESSAGE);
            return false;
        }
        return properties.isTextValueDefaultNull();
    }

    public static boolean isMapValue() {
        if (properties == null) {
            logger.warn(WARNING_MESSAGE);
            return false;
        }
        return properties.isMapValue();
    }

    public static boolean isReplaceValue() {
        if (properties == null) {
            logger.warn(WARNING_MESSAGE);
            return false;
        }
        return properties.isReplaceValue();
    }

    public static Optional<DictProperties> properties() {
        return Optional.ofNullable(properties);
    }

    public static <U> Optional<U> get(Function<DictProperties, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (properties == null) {
            logger.warn(WARNING_MESSAGE);
            return Optional.empty();
        }
        return Optional.ofNullable(mapper.apply(properties));
    }

    public static <T> T getBean(final Class<T> clazz) {
        final String[] beanNamesForType = applicationContext.getBeanNamesForType(clazz);
        if (beanNamesForType.length == 0) {
            return null;
        }
        return applicationContext.getBean(beanNamesForType[0], clazz);
    }

    /**
     * 当环境中不存在 RemoteDic Bean 的时候创建一个默认的 RemoteDic Bean 实例。用来获取不存在系统字典的字典数据
     *
     * @return {@link RemoteDict}
     */
    @ConditionalOnMissingBean
    @Bean
    public RemoteDict remoteDict() {
        return new RemoteDictDefaultImpl();
    }

    /**
     * 初始化工具类
     *
     * @param dictRegistrar 系统字典注册器
     * @param store         系统字典存储器
     * @param cacheFactory  系统字典缓存构建
     * @return DictUtil
     */
    @Bean
    public DictUtil dictUtil(final DictRegistrar dictRegistrar, final DictStore store, final DictCacheFactory cacheFactory) {
        return new DictUtil(dictRegistrar, store, cacheFactory);
    }

    /**
     * 数据字典 JacksonModule
     *
     * @return DictJacksonModule
     */
    @Bean
    public DictJacksonModule dictJacksonModule() {
        return new DictJacksonModule();
    }

    /**
     * 获取系统字典的 Controller 层 API 接口
     *
     * @return Controller
     */
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "system.dict.controller", name = "enabled", matchIfMissing = true)
    @Bean
    public DictController dictController() {
        return new DictController();
    }
}
