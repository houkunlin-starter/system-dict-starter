package com.houkunlin.dict.properties;

import lombok.*;

import static com.houkunlin.dict.DictUtil.*;

/**
 * 缓存存储键前缀配置类
 * <p>
 * 该类用于配置数据字典在缓存中存储时使用的键前缀。
 * 通过不同的前缀可以区分不同类型的字典数据，避免键名冲突。
 * </p>
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
     * <p>
     * 涵盖所有的字典类型，包括普通字典（手动维护类型的）和系统字典。
     * 用于在缓存中存储字典类型信息时的键前缀。
     * </p>
     */
    private String typePrefix = TYPE_PREFIX;
    /**
     * 系统字典类型前缀
     * <p>
     * 单独存储一份系统字典类型前缀，以便给有需要单独读取系统字典类型使用。
     * 系统字典通常指由系统自动生成的字典类型，与普通手动维护的字典类型区分开。
     * </p>
     *
     * @since 1.5.0
     */
    private String typeSystemPrefix = TYPE_SYSTEM_PREFIX;
    /**
     * 字典值前缀
     * <p>
     * 用于在缓存中存储字典值信息时的键前缀。
     * 字典值通常包含字典文本、额外属性等信息。
     * </p>
     */
    private String valuePrefix = VALUE_PREFIX;
    /**
     * 树结构父级字典值前缀
     * <p>
     * 用于在缓存中存储树形结构字典的父级关系信息时的键前缀。
     * 树形字典需要记录父子关系，以便快速构建树形结构。
     * </p>
     */
    private String parentPrefix = PARENT_PREFIX;
}
