package com.houkunlin.system.dict.starter.properties;

import com.github.benmanes.caffeine.cache.CaffeineSpec;
import com.houkunlin.system.dict.starter.DictUtil;
import lombok.*;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * <p>获取字典值缓存配置。配置在使用 {@link DictUtil} 获取字典文本时是否使用缓存。</p>
 * <p>{@link com.houkunlin.system.dict.starter.json.DictText} 最后的处理实际也会调用 {@link DictUtil} 来获取字典文本信息。</p>
 * <p>在使用 {@link com.houkunlin.system.dict.starter.store.LocalDictStore} 存储时是否启用缓存影响不大，</p>
 * <p>
 * 但是在使用 {@link com.houkunlin.system.dict.starter.store.RedisDictStore} 存储时，列表页场景可能会有较大的影响，
 * 因为每次获取字典文本都会从Redis中调用，此时假如字典值一致的时候会频繁重复调用，因此增加耗时。
 * </p>
 * <p>启用缓存功能能够有效的解决相同字典值重复从Redis读取数据导致耗时增加问题。</p>
 *
 * @author HouKunLin
 * @since 1.4.2
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DictPropertiesCache {
    public static final String DEFAULT_CAFFEINE_SPEC = "maximumSize=500,initialCapacity=50,expireAfterWrite=30s";
    /**
     * 是否启用缓存
     */
    private boolean enabled = true;
    /**
     * 缓存最大容量
     */
    @Deprecated(since = "1.6.4", forRemoval = true)
    private int maximumSize = 500;
    /**
     * 缓存初始化容量
     */
    @Deprecated(since = "1.6.4", forRemoval = true)
    private int initialCapacity = 50;
    /**
     * 有效期时长
     */
    @Deprecated(since = "1.6.4", forRemoval = true)
    private Duration duration = Duration.ofSeconds(30);
    /**
     * 在有效期内同一个字典值未命中指定次数  将快速返回，不再重复请求获取数据字典信息。
     * 例如：一个 userType 类型 值为 2 的字典，在 30 秒内超过 50 次找不到字典文本，那么在本次 30 秒的周期内将不再继续请求字典信息，而是直接返回一个 null 值。
     * 特别是在使用 Redis 存储数据字典信息时，频繁未命中数据将会频繁进行网络IO，因此可能会增加单个接口返回数据的耗时（数据量大转换次数多时）
     */
    private int missNum = 50;
    /**
     * Caffeine Spec 字符串参数，默认值：maximumSize=500,initialCapacity=50,expireAfterWrite=30s
     *
     * @see CaffeineSpec
     * @see CaffeineSpec#parse(String)
     */
    @NestedConfigurationProperty
    private Caffeine caffeine = new Caffeine();

    /**
     * 是否设置了 Caffeine Spec 参数
     *
     * @return 是否设置了 Caffeine Spec 参数
     */
    public boolean isUseCaffeineSpec() {
        return this.caffeine != null && StringUtils.hasText(this.caffeine.spec);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Caffeine {

        /**
         * Caffeine Spec 字符串参数，默认值：maximumSize=500,initialCapacity=50,expireAfterWrite=30s
         *
         * @see CaffeineSpec
         * @see CaffeineSpec#parse(String)
         */
        private String spec;
    }
}
