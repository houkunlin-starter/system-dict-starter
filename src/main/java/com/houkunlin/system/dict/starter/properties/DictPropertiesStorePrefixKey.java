package com.houkunlin.system.dict.starter.properties;

import lombok.*;

import static com.houkunlin.system.dict.starter.DictUtil.*;

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
     * 字典类型前缀，涵盖所有的字典类型（普通字典（手动维护类型的）和系统字典）
     */
    private String typePrefix = TYPE_PREFIX;
    /**
     * 系统字典类型前缀，单独存储一份，以便给有需要单独读取系统字典类型使用
     * @since 1.5.0
     */
    private String typeSystemPrefix = TYPE_SYSTEM_PREFIX;
    /**
     * 字典值前缀
     */
    private String valuePrefix = VALUE_PREFIX;
    /**
     * 树结构父级字典值前缀
     */
    private String parentPrefix = PARENT_PREFIX;
}
