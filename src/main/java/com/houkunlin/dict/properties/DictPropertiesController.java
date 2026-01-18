package com.houkunlin.dict.properties;

import lombok.*;

/**
 * 默认WEB请求控制器配置类
 * <p>
 * 该类用于配置数据字典的WEB接口相关参数，包括是否启用接口以及接口前缀。
 * 通过配置可以灵活控制数据字典对外提供的RESTful接口。
 * </p>
 *
 * @author HouKunLin
 * @since 1.4.1
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DictPropertiesController {
    /**
     * 是否启用 WEB 请求接口
     * <p>
     * 控制是否启用数据字典的WEB接口。当设置为 {@code false} 时，所有数据字典相关的RESTful接口将不可用。
     * 在生产环境中，可以根据安全需求决定是否启用这些接口。
     * </p>
     */
    private boolean enabled = true;
    /**
     * WEB 请求接口前缀
     * <p>
     * 数据字典WEB接口的URL前缀。所有数据字典相关的接口都会以此前缀开头。
     * 例如，当设置为 {@code "/dict"} 时，获取字典类型的接口路径为 {@code "/dict/{type}"}。
     * </p>
     */
    private String prefix = "/dict";
}
