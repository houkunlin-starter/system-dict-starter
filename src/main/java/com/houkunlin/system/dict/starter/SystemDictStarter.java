package com.houkunlin.system.dict.starter;

import com.houkunlin.system.dict.starter.properties.DictProperties;
import com.houkunlin.system.dict.starter.store.RemoteDict;
import com.houkunlin.system.dict.starter.store.RemoteDictDefaultImpl;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * 自动扫描配置系统注解所需要的Bean对象
 *
 * @author HouKunLin
 */
@Getter
@Configuration(proxyBeanMethods = false)
@ComponentScan
public class SystemDictStarter {
    private static final Logger logger = LoggerFactory.getLogger(SystemDictStarter.class);
    private static final String WARNING_MESSAGE = "DictProperties 未找到，请在启动类添加 @SystemDictScan 注解启用相关服务";
    private static DictProperties properties;

    public SystemDictStarter(@Lazy final DictProperties properties) {
        SystemDictStarter.properties = properties;
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
}
