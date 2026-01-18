package com.houkunlin.dict;

import com.houkunlin.dict.cache.DictCacheFactory;
import com.houkunlin.dict.jackson.DictJsonMapperBuilderCustomizer;
import com.houkunlin.dict.properties.DictProperties;
import com.houkunlin.dict.store.DictStore;
import com.houkunlin.dict.store.RemoteDict;
import com.houkunlin.dict.store.RemoteDictDefaultImpl;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
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
 * 系统字典自动配置类
 * <p>自动配置系统字典相关的Bean对象，包括字典注册器、存储器、缓存工厂等</p>
 * <p>使用 @SystemDictScan 注解启用相关服务</p>
 *
 * @author HouKunLin
 */
@Getter
@Configuration(proxyBeanMethods = false)
@Import(DictProperties.class)
public class SystemDictAutoConfiguration {
    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(SystemDictAutoConfiguration.class);
    /**
     * 警告消息：当 DictProperties 未找到时的提示信息
     */
    private static final String WARNING_MESSAGE = "DictProperties 未找到，请在启动类添加 @SystemDictScan 注解启用相关服务";
    /**
     * 字典配置属性
     */
    private static DictProperties properties;
    /**
     * 应用上下文
     */
    private static ApplicationContext applicationContext;

    /**
     * 获取是否使用原始值
     *
     * @return 是否使用原始值
     */
    public static boolean isRawValue() {
        if (properties == null) {
            logger.warn(WARNING_MESSAGE);
            return false;
        }
        return properties.isRawValue();
    }

    /**
     * 获取文本值默认是否为 null
     *
     * @return 文本值默认是否为 null
     */
    public static boolean isTextValueDefaultNull() {
        if (properties == null) {
            logger.warn(WARNING_MESSAGE);
            return false;
        }
        return properties.isTextValueDefaultNull();
    }

    /**
     * 获取是否使用 Map 值
     *
     * @return 是否使用 Map 值
     */
    public static boolean isMapValue() {
        if (properties == null) {
            logger.warn(WARNING_MESSAGE);
            return false;
        }
        return properties.isMapValue();
    }

    /**
     * 获取是否替换值
     *
     * @return 是否替换值
     */
    public static boolean isReplaceValue() {
        if (properties == null) {
            logger.warn(WARNING_MESSAGE);
            return false;
        }
        return properties.isReplaceValue();
    }

    /**
     * 获取字典配置属性
     *
     * @return 字典配置属性
     */
    public static Optional<DictProperties> properties() {
        return Optional.ofNullable(properties);
    }

    /**
     * 通过映射函数获取字典配置属性的某个值
     *
     * @param <U>    返回值类型
     * @param mapper 映射函数
     * @return 映射后的值
     */
    public static <U> Optional<U> get(Function<DictProperties, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (properties == null) {
            logger.warn(WARNING_MESSAGE);
            return Optional.empty();
        }
        return Optional.ofNullable(mapper.apply(properties));
    }

    /**
     * 获取指定类型的 Bean
     *
     * @param <T>   Bean类型
     * @param clazz Bean类型
     * @return Bean实例
     */
    public static <T> T getBean(final Class<T> clazz) {
        final String[] beanNamesForType = applicationContext.getBeanNamesForType(clazz);
        if (beanNamesForType.length == 0) {
            return null;
        }
        return applicationContext.getBean(beanNamesForType[0], clazz);
    }

    /**
     * 获取指定类型的Bean提供者
     *
     * @param <T>   Bean类型
     * @param clazz Bean类型
     * @return Bean提供者
     */
    public static <T> ObjectProvider<T> getBeanOfType(final Class<T> clazz) {
        return applicationContext.getBeanProvider(clazz);
    }

    /**
     * 设置字典配置属性
     *
     * @param properties 字典配置属性
     */
    @Autowired
    public void setProperties(DictProperties properties) {
        SystemDictAutoConfiguration.properties = properties;
        DictUtil.initPrefix(properties.getStoreKey());
    }

    /**
     * 设置应用上下文
     *
     * @param applicationContext 应用上下文
     */
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        SystemDictAutoConfiguration.applicationContext = applicationContext;
    }

    /**
     * 当环境中不存在 RemoteDict Bean 的时候创建一个默认的 RemoteDict Bean 实例
     * 用来获取不存在系统字典的字典数据
     *
     * @return {@link RemoteDict} 远程字典实例
     */
    @ConditionalOnMissingBean
    @Bean
    public RemoteDict remoteDict() {
        return new RemoteDictDefaultImpl();
    }

    /**
     * 初始化字典工具类
     *
     * @param dictRegistrar 系统字典注册器
     * @param store         系统字典存储器
     * @param cacheFactory  系统字典缓存工厂
     * @return {@link DictUtil} 字典工具类实例
     */
    @Bean
    public DictUtil dictUtil(final DictRegistrar dictRegistrar, final DictStore store, final DictCacheFactory cacheFactory) {
        return new DictUtil(dictRegistrar, store, cacheFactory);
    }

    /**
     * 数据字典JSONMapper初始化处理
     * 用于配置Jackson序列化时的字典值处理
     *
     * @return {@link DictJsonMapperBuilderCustomizer} JSON映射构建器自定义器
     */
    @Bean
    public DictJsonMapperBuilderCustomizer dictJsonMapperBuilderCustomizer() {
        return new DictJsonMapperBuilderCustomizer();
    }

    /**
     * 获取系统字典的 Controller 层 API 接口
     * 提供字典相关的REST接口
     *
     * @return {@link DictController} 字典控制器实例
     */
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "system.dict.controller", name = "enabled", matchIfMissing = true)
    @Bean
    public DictController dictController() {
        return new DictController();
    }
}
