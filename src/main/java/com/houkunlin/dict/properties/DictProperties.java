package com.houkunlin.dict.properties;

import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.enums.BytecodeType;
import com.houkunlin.dict.enums.MqType;
import com.houkunlin.dict.enums.StoreType;
import com.houkunlin.dict.notice.RefreshDictEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * 系统数据字典配置文件配置信息对象
 * <p>
 * 该类是系统数据字典的核心配置类，通过 {@code @ConfigurationProperties} 注解绑定到 {@code system.dict} 配置前缀。
 * 包含了数据字典处理的各种配置选项，如字典值转换方式、缓存配置、消息队列配置等。
 * </p>
 *
 * @author HouKunLin
 * @since 1.0.0
 */
@Data
@ToString
@EqualsAndHashCode
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties("system.dict")
public class DictProperties {
    /**
     * 默认 MQ 交换器名称
     * <p>
     * 当使用消息队列通知其他系统刷新字典时，默认使用的 FanoutExchange 交换器名称。
     * 该交换器用于广播字典刷新事件到所有订阅的系统。
     * </p>
     */
    public static final String DEFAULT_MQ_EXCHANGE_NAME = "app.dict.fanout.refreshDict";
    /**
     * 是否显示原生数据字典值。true 实际类型转换，false 转换成字符串值
     *
     * @param rawValue true 实际类型转换，false 转换成字符串值
     * @return true 实际类型转换，false 转换成字符串值
     */
    private boolean rawValue = false;
    /**
     * 是否把字典值转换成 Map 形式，包含字典值和文本。
     *
     * @param mapValue 是否返回 map 对象
     * @return 是否返回 map 对象
     */
    private boolean mapValue = false;
    /**
     * 是否用字典文本替换字典值输出（在原字段输出字典文本）
     *
     * @param replaceValue 是否替换原字段值
     * @return 是否替换原字段值
     */
    private boolean replaceValue = false;
    /**
     * 为防止陷入死循环，请设置树形结构数据的向访问的最大访问深度，超过最大访问深度则直接返回。
     * int &lt;= 0 视为不限制深度
     *
     * @see DictText#treeDepth()
     * @since 1.4.6.1
     * @param treeDepth 树形结构深度
     * @return 树形结构深度
     */
    private int treeDepth = -1;
    /**
     * 字典文本的值是否默认为null，true 默认为null，false 默认为空字符串
     *
     * @param textValueDefaultNull true 默认为null，false 默认为空字符串
     * @return true 默认为null，false 默认为空字符串
     */
    private boolean textValueDefaultNull = false;
    // =================================================
    /**
     * 是否在启动的时候刷新字典；
     * 设置为 true 时在数据量大的时候可能会影响系统启动速度，可在系统启动后发起 {@link RefreshDictEvent} 事件通知刷新字典
     *
     * @param onBootRefreshDict 是否在启动的时候刷新字典
     * @return 是否在启动的时候刷新字典
     */
    private boolean onBootRefreshDict = true;
    /**
     * 通知其他协同系统刷新字典的MQ类型
     *
     * @since 1.4.4
     * @param mqType MQ 类型
     * @return MQ 类型
     */
    private MqType mqType = MqType.NONE;
    /**
     * 配置使用 Map 、 Redis 来存储字典数据
     *
     * @since 1.4.11
     * @param storeType 数据字典存储器类型
     * @return 数据字典存储器类型
     */
    private StoreType storeType = StoreType.AUTO;
    /**
     * 消息队列 FanoutExchange 交换器名称
     * <p>
     * 在多系统协同的时候刷新字典的时候会用到。当系统需要通知其他系统刷新字典时，
     * 会向该交换器发布消息，所有订阅了该交换器的系统都会收到刷新通知。
     * </p>
     *
     * @param mqExchangeName 交换器名称
     * @return 交换器名称
     */
    private String mqExchangeName = DEFAULT_MQ_EXCHANGE_NAME;
    /**
     * 两次刷新字典事件的时间间隔
     * <p>
     * 两次刷新事件时间间隔小于配置参数将不会刷新，用于防止短时间内重复刷新字典。
     * 此设置只影响 {@link RefreshDictEvent} 事件，可以有效避免因频繁触发刷新事件导致的性能问题。
     * </p>
     *
     * @param refreshDictInterval 刷新间隔时间，防止重复刷新，在刷新间隔内多次调用刷新时，只有第一次有效
     * @return 刷新间隔时间，防止重复刷新，在刷新间隔内多次调用刷新时，只有第一次有效
     */
    private Duration refreshDictInterval = Duration.ofSeconds(60);
    /**
     * 缓存配置
     *
     * @param cache 缓存配置
     * @return 缓存配置
     */
    @NestedConfigurationProperty
    private DictPropertiesCache cache = new DictPropertiesCache();
    /**
     * 默认控制器接口
     *
     * @param controller 接口配置
     * @return 接口配置
     */
    @NestedConfigurationProperty
    private DictPropertiesController controller = new DictPropertiesController();
    /**
     * 缓存存储 键前缀
     *
     * @since 1.4.7
     * @param storeKey 缓存KEY前缀信息
     * @return 缓存KEY前缀信息
     */
    @NestedConfigurationProperty
    private DictPropertiesStorePrefixKey storeKey = new DictPropertiesStorePrefixKey();
    /**
     * 选择所使用字节码技术
     * <p>
     * 默认会使用 ASM 字节码技术。该配置用于选择动态生成字典转换器时使用的字节码技术。
     * 不同的字节码技术在性能、兼容性和功能支持上有所差异。
     * </p>
     *
     * @since 1.4.8
     * @param bytecode 动态字节码技术类型
     * @return 动态字节码技术类型
     */
    private BytecodeType bytecode = BytecodeType.ASM;
}
