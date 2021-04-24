package com.houkunlin.system.dic.starter;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 系统数据字典配置文件配置信息对象
 *
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
    /**
     * 消息队列 FanoutExchange 交换器名称. 在多系统协同的时候刷新字典的时候会用到
     */
    private String mqExchangeName = "app.dic.fanout.refreshDic";
    /**
     * 刷新日志消息的Header配置，存储标记消息来源应用名称的 Header KEY
     */
    private String mqHeaderSourceKey = "SourceApplicationName";
}
