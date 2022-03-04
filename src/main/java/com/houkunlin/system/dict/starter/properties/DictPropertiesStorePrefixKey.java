package com.houkunlin.system.dict.starter.properties;

import lombok.*;

/**
 * 缓存存储键前缀
 *
 * @author HouKunLin
 * @since 1.4.7
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DictPropertiesStorePrefixKey {
    /**
     * 字典类型前缀
     */
    private String typePrefix = "dict:t:";
    /**
     * 字典值前缀
     */
    private String valuePrefix = "dict:v:";
    /**
     * 树结构父级字典值前缀
     */
    private String parentPrefix = "dict:p:";
}
