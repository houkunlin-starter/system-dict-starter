package com.houkunlin.system.dict.starter.properties;

import com.houkunlin.system.dict.starter.json.DictText;
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
     * 是否把字典值转换成 Map 形式，包含字典值和文本。
     */
    private boolean mapValue = false;
    /**
     * 是否用字典文本替换字典值输出（在原字段输出字典文本）
     */
    private boolean replaceValue = false;
    /**
     * 为防止陷入死循环，请设置树形结构数据的向访问的最大访问深度，超过最大访问深度则直接返回。
     * int <= 0 视为不限制深度
     *
     * @see DictText#treeDepth()
     * @since 1.4.6.1
     */
    private int treeDepth = -1;
    /**
     * 字典文本的值是否默认为null，true 默认为null，false 默认为空字符串
     */
    private boolean textValueDefaultNull = false;
    // =================================================
    /**
     * 是否在启动的时候刷新字典；
     * 设置为 true 时在数据量大的时候可能会影响系统启动速度，可在系统启动后发起 {@link RefreshDictEvent} 事件通知刷新字典
     */
    private boolean onBootRefreshDict = true;
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
     * 两次刷新字典事件的时间间隔；两次刷新事件时间间隔小于配置参数将不会刷新。
     * 此设置只影响 {@link RefreshDictEvent} 事件
     */
    private Duration refreshDictInterval = Duration.ofSeconds(60);
    /**
     * 缓存配置
     */
    @NestedConfigurationProperty
    private DictPropertiesCache cache = new DictPropertiesCache();
    /**
     * 默认控制器接口
     */
    @NestedConfigurationProperty
    private DictPropertiesController controller = new DictPropertiesController();
    /**
     * 缓存存储 键前缀
     *
     * @since 1.4.7
     */
    @NestedConfigurationProperty
    private DictPropertiesStorePrefixKey storeKey = new DictPropertiesStorePrefixKey();
}
