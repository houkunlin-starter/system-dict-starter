package com.system.dic.starter;

import com.system.dic.starter.store.DicStore;
import com.system.dic.starter.store.LocalStore;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;

/**
 * 自动扫描配置系统注解所需要的Bean对象
 *
 * @author HouKunLin
 */
@Getter
@ComponentScan
public class SystemDicStarter {
    private static final Logger logger = LoggerFactory.getLogger(SystemDicStarter.class);
    private static DicProperties dicProperties;

    public SystemDicStarter(@Lazy final DicProperties dicProperties) {
        SystemDicStarter.dicProperties = dicProperties;
    }

    public static boolean isRawValue() {
        if (dicProperties == null) {
            logger.warn("DicProperties Not Found");
            return false;
        }
        return dicProperties.isRawValue();
    }

    public static boolean isTextValueDefaultNull() {
        if (dicProperties == null) {
            logger.warn("DicProperties Not Found");
            return false;
        }
        return dicProperties.isTextValueDefaultNull();
    }

    /**
     * 当环境中不存在 DicStore Bean 的时候创建一个默认的 DicStore Bean 实例
     *
     * @return DicStore
     */
    @ConditionalOnMissingBean
    @Bean
    public DicStore dicStore() {
        logger.debug("使用默认的本地存储来存储数据字典信息");
        return new LocalStore();
    }
}
