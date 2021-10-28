package com.houkunlin.system.dict.starter.properties;

import com.houkunlin.system.dict.starter.notice.RefreshDictEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * 系统数据字典配置文件配置信息对象
 *
 * @author HouKunLin
 */
@Data
@ToString
@EqualsAndHashCode
@Configuration
@ConfigurationProperties("system.dict")
public class DictProperties {
    public static final String DEFAULT_MQ_EXCHANGE_NAME = "app.dict.fanout.refreshDict";
    /**
     * 是否显示原生数据字典值。true 实际类型转换，false 转换成字符串值
     */
    private boolean rawValue = false;
    /**
     * 字典文本的值是否默认为null，true 默认为null，false 默认为空字符串
     */
    private boolean textValueDefaultNull = false;
    /**
     * 是否在启动的时候刷新字典；
     * 设置为 true 时在数据量大的时候可能会影响系统启动速度，可在系统启动后发起 {@link RefreshDictEvent} 事件通知刷新字典
     */
    private boolean onBootRefreshDict = true;
    /**
     * 是否把字典值转换成 Map 形式，包含字典值和文本。
     */
    private boolean mapValue = false;
    /**
     * 通知其他协同系统刷新字典的MQ类型
     *
     * @since 1.4.4
     */
    private MqType mqType = MqType.NONE;
    /**
     * 消息队列 FanoutExchange 交换器名称. 在多系统协同的时候刷新字典的时候会用到
     */
    private String mqExchangeName = DEFAULT_MQ_EXCHANGE_NAME;
    /**
     * 两次刷新字典事件的时间间隔；两次刷新事件时间间隔小于配置参数将不会刷新
     */
    private Duration refreshDictInterval = Duration.ofSeconds(60);

    @NestedConfigurationProperty
    private DictPropertiesCache cache = new DictPropertiesCache();

    @NestedConfigurationProperty
    private DictPropertiesController controller = new DictPropertiesController();
}
