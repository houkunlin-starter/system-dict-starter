package com.system.dic.starter;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
}
