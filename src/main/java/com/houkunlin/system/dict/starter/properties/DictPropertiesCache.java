package com.houkunlin.system.dict.starter.properties;

import com.houkunlin.system.dict.starter.DictUtil;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
@Configuration
@ConfigurationProperties("system.dict.cache")
public class DictPropertiesCache {
    /**
     * 是否启用缓存
     */
    private boolean enabled = true;
    /**
     * 缓存最大容量
     */
    private int maximumSize = 500;
    /**
     * 缓存初始化容量
     */
    private int initialCapacity = 50;
    /**
     * 有效期时长
     */
    private Duration duration = Duration.ofSeconds(30);
}
