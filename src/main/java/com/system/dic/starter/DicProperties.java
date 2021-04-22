package com.system.dic.starter;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author HouKunLin
 */
@Data
@ToString
@Configuration
@ConfigurationProperties("system.dic")
public class DicProperties {
    /**
     * 是否显示原生数据字典值。true 实际类型转换，false 转换成字符串值
     */
    private boolean rawValue = false;
    /**
     * 字典文本的值是否默认为null，true 默认为null，false 默认为空字符串
     */
    private boolean textValueDefaultNull = false;
}
