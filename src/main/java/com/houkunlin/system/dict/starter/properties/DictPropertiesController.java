package com.houkunlin.system.dict.starter.properties;

import lombok.*;

/**
 * 默认WEB请求控制器配置
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
     */
    private boolean enabled = true;
    /**
     * WEB 请求接口前缀
     */
    private String prefix = "/dict";
}
