package com.houkunlin.system.dict.starter.properties;

/**
 * 存储字典数据的存储类型
 *
 * @author HouKunLin
 * @since 1.4.11
 */
public enum StoreType {
    /**
     * （默认）存在 Redis 就使用 Redis，否则使用 Local 的 Map 来存储字典数据
     */
    AUTO,
    /**
     * 使用本地的 Map 来存储字典数据
     */
    LOCAL,
    /**
     * 使用 Redis 来存储字典数据
     */
    REDIS,
    ;
}
